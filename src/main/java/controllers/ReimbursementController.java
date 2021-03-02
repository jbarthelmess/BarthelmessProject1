package controllers;

import com.google.gson.Gson;
import entities.User;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import services.ReimbursementService;
import utils.JwtUtil;

public class ReimbursementController {
    private final ReimbursementService reimbursementService;
    private final Gson gson = new Gson();

    public ReimbursementController(ReimbursementService reimbursementService) {
        if(reimbursementService == null) {
            throw new NullPointerException("Reimbursement Service cannot be null");
        }
        this.reimbursementService = reimbursementService;
    }

    private boolean verifyAuthentication(Context ctx) {
        String authorization = ctx.header("Authorization");
        JwtUtil.isValidJWT(authorization);
        return false;
    }

    public Handler getUser = (ctx) -> {
        User user = gson.fromJson(ctx.body(), User.class);
    };

    public Handler getUserLogin = (ctx) -> {

    };
}
