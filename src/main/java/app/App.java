package app;

import controllers.ReimbursementController;
import daos.ReimbursementDAO;
import daos.ReimbursementDaoImpl;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import org.apache.log4j.Logger;
import services.ReimbursementService;
import services.ReimbursementServiceImpl;

public class App {
    static Logger logger = Logger.getLogger(App.class.getName());
    public static void main(String [] args) {
        Javalin app = Javalin.create(JavalinConfig::enableCorsForAllOrigins);

        try {
            ReimbursementDAO dao = new ReimbursementDaoImpl();
            ReimbursementService service = new ReimbursementServiceImpl(dao);
            ReimbursementController controller = new ReimbursementController(service);

            app.post("/users/login", controller.getUserLogin);

            app.get("/users/:userId", controller.getUser);
            app.post("/users/:userId/expense", controller.createExpense);
            app.get("/users/:userId/expense/:expenseId", controller.getExpense);
            app.put("/users/:userId/expense/:expenseId", controller.updateExpense);
            app.get("/users/:userId/expense", controller.getAllExpenses);

            app.start();
        } catch (NullPointerException n) {
            logger.error(n.getMessage());
        }
    }
}
