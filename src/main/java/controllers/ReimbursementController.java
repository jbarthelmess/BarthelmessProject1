package controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import entities.Expense;
import entities.LoginAttempt;
import entities.ManagerStatistics;
import entities.User;
import io.javalin.core.util.FileUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import org.apache.log4j.Logger;
import services.ReimbursementService;
import utils.JwtUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

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
        if(authorization == null) {
            logger.warn("Attempted access with no verification");
            return null;
        }
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
        try {
            Expense expense = gson.fromJson(ctx.body(), Expense.class);
            if(expense == null) {
                ctx.status(400);
                ctx.result("No expense info provided");
                return;
            }
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
        } catch (JsonSyntaxException n) {
            ctx.status(400);
            ctx.result(n.getMessage());
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
        } catch (IllegalArgumentException n) {
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
            Set<Expense> expenses = service.getAllExpenses(user);
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
        } catch (IllegalArgumentException n) {
            ctx.status(400);
            ctx.result(n.getMessage());
        } catch (NullPointerException e) {
            ctx.status(400);
            ctx.result("No expense info given");
        }
    };

    public Handler getUserLogin = ctx -> {
        LoginAttempt login = gson.fromJson(ctx.body(), LoginAttempt.class);
        if(login == null) {
            ctx.status(400);
            ctx.result("No login information provided");
            logger.warn("Login attempt made with empty request payload");
            return;
        }
        try{
            User user = service.login(login);
            if (user == null) {
                ctx.status(403);
                ctx.result("Login failed");
                logger.warn("Failed attempted login with username: " + login.getUsername());
                return;
            }
            String jwt = JwtUtil.generate(user);
            user.setJwt(jwt);
            ctx.status(200);
            ctx.result(gson.toJson(user));
            logger.info("Login for user " + login.getUsername());
        } catch (IllegalAccessException i) {
            ctx.status(403);
            ctx.result(i.getMessage());
        }
    };

    public Handler getManagerStatistics = ctx -> {
        User user = verifyAuthentication(ctx);
        if(user == null) {
            ctx.status(403);
            ctx.result("Missing or invalid JWT. Please log in");
            return;
        }
        try {
            ManagerStatistics stats = service.getManagerStatistics(user);
            if(stats == null) {
                ctx.status(400);
                ctx.result("Could not obtain statistics");
                return;
            }
            ctx.status(200);
            ctx.result(gson.toJson(stats));
        } catch (IllegalAccessException i) {
            ctx.status(403);
            ctx.result(i.getMessage());
        }
    };

    public Handler uploadFiles = ctx -> {
        User user = verifyAuthentication(ctx);
        if(user == null) {
            ctx.status(403);
            ctx.result("Missing or invalid JWT. Please log in");
            return;
        }
        try {
            List<UploadedFile> files = ctx.uploadedFiles("file");
            File file = new File("uploads/"+files.get(0).getFilename());
            if(!file.createNewFile()) {
                System.out.println("File creation failed");
                ctx.status(400);
                ctx.result("Could not make new file");
            }
            System.out.println("Streaming input to "+file.getPath());
            System.out.println("Context object");
            FileUtil.streamToFile(files.get(0).getContent(), file.getPath());
            System.out.println("File streamed successfully");
            String fileURL = service.uploadFile(file, user);
            System.out.println("file is stored at "+fileURL);
            ctx.status(201);
            ctx.result(fileURL);
        } catch (NullPointerException | IOException n) {
            ctx.status(400);
            ctx.result(n.getMessage());
        }
    };
}
