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

            app.get("/users", controller.getUser);
            app.post("/users/expense", controller.createExpense);
            app.get("/users/expense/:expenseId", controller.getExpense);
            app.put("/users/expense/:expenseId", controller.updateExpense);
            app.get("/users/expense", controller.getAllExpenses);
            app.get("/users/statistics", controller.getManagerStatistics);

            app.start();
        } catch (NullPointerException n) {
            logger.error(n.getMessage());
        }
    }
}
