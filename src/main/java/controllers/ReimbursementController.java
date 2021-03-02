package controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import entities.Expense;
import entities.LoginAttempt;
import entities.User;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.apache.log4j.Logger;
import services.ReimbursementService;
import utils.JwtUtil;

import java.util.HashSet;

public class ReimbursementController {
    private ReimbursementService service;
    private final Gson gson = new Gson();
    static Logger logger = Logger.getLogger(ReimbursementController.class.getName());

    public ReimbursementController(ReimbursementService reimbursementService) {
        if(reimbursementService == null) {
            throw new NullPointerException("Reimbursement Service cannot be null");
        }
        service = reimbursementService;
    }

    private User verifyAuthentication(Context ctx) {
        String authorization = ctx.header("Authorization");
        DecodedJWT jwt = JwtUtil.isValidJWT(authorization);
        if(jwt == null) {
            logger.warn("Attempted access without verification");
            return null;
        }
        User user = new User();
        user.setUserId(jwt.getClaim("userId").asInt());
        user.setUsername(jwt.getClaim("username").asString());
        user.setManager(jwt.getClaim("isManager").asBoolean());
        return user;
    }

    public Handler getUser = (ctx) -> {
        User user = verifyAuthentication(ctx);
        if(user == null) {
            ctx.status(403);
            ctx.result("Missing or invalid JWT. Please log in");
            return;
        }
        User fullUser = service.getUser(user);
        if(fullUser == null) {
            ctx.status(404);
            ctx.result("Could not find user in the database");
            return;
        }
        ctx.status(200);
        ctx.result(gson.toJson(fullUser));
    };

    public Handler createExpense = (ctx) -> {
        User user = verifyAuthentication(ctx);
        if(user == null) {
            ctx.status(403);
            ctx.result("Missing or invalid JWT. Please log in");
            return;
        }
        Expense expense = gson.fromJson(ctx.body(), Expense.class);
        try {
            Expense createdExpense = service.createExpense(user, expense);
            if (createdExpense == null) {
                ctx.status(500);
                ctx.result("Could not create expense");
                return;
            }
            ctx.status(201);
            ctx.result(gson.toJson(createdExpense));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            ctx.status(400);
            ctx.result(e.getMessage());
        }
    };

    public Handler getExpense = (ctx) -> {
        User user = verifyAuthentication(ctx);
        if(user == null) {
            ctx.status(403);
            ctx.result("Missing or invalid JWT. Please log in");
            return;
        }
        try {
            Expense expense = new Expense();
            expense.setExpenseId(Integer.parseInt(ctx.pathParam("expenseId")));
            Expense fullExpense = service.getExpense(user, expense.getExpenseId());
            if(fullExpense == null) {
                ctx.status(404);
                ctx.result("Expense not found");
                return;
            }
            ctx.status(200);
            ctx.result(gson.toJson(fullExpense));
        } catch (IllegalAccessException i) {
            ctx.status(403);
            ctx.result(i.getMessage());
        } catch (NumberFormatException n) {
            ctx.status(400);
            ctx.result(n.getMessage());
        }
    };

    public Handler getAllExpenses = (ctx) -> {
        User user = verifyAuthentication(ctx);
        if(user == null) {
            ctx.status(403);
            ctx.result("Missing or invalid JWT. Please log in");
            return;
        }
        try {
            HashSet<Expense> expenses = service.getAllExpenses(user);
            if(expenses == null) {
                ctx.status(500);
                ctx.result("Could not retrieve expenses");
                return;
            }
            ctx.status(200);
            ctx.result(gson.toJson(expenses));
        } catch (IllegalAccessException i ) {
            ctx.status(403);
            ctx.result(i.getMessage());
        }
    };

    public Handler updateExpense = (ctx) -> {
        User user = verifyAuthentication(ctx);
        if(user == null) {
            ctx.status(403);
            ctx.result("Missing or invalid JWT. Please log in");
            return;
        }
        try {
            Expense expense = gson.fromJson(ctx.body(), Expense.class);
            expense.setExpenseId(Integer.parseInt(ctx.pathParam("expenseId")));
            Expense updatedExpense = service.updateExpense(user, expense);
            if(updatedExpense == null) {
                ctx.status(404);
                ctx.result("Could not update expense or expense doesn't exist");
                return;
            }
            ctx.status(200);
            ctx.result(gson.toJson(updatedExpense));
        } catch (IllegalAccessException i) {
            ctx.status(403);
            ctx.result(i.getMessage());
        } catch (NumberFormatException n) {
            ctx.status(400);
            ctx.result(n.getMessage());
        }
    };

    public Handler getUserLogin = (ctx) -> {
        LoginAttempt login = gson.fromJson(ctx.body(), LoginAttempt.class);
        User user = service.login(login);
        if(user == null) {
            ctx.status(403);
            ctx.result("Login failed");
            logger.warn("Failed attempted login with username: "+login.getUsername());
            return;
        }
        String jwt = JwtUtil.generate(user);
        ctx.status(200);
        ctx.result(jwt);
        logger.info("Login for user "+login.getUsername());
    };
}
