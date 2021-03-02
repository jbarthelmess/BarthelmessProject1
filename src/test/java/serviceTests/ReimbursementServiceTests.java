package serviceTests;

//import com.fasterxml.jackson.annotation.JsonTypeInfo;
import daos.ReimbursementDAO;
import daos.ReimbursementDaoImpl;
import entities.Expense;
import entities.User;
import org.junit.jupiter.api.*;
import services.ReimbursementService;
import services.ReimbursementServiceImpl;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReimbursementServiceTests {
    private static final ReimbursementDAO dao = new ReimbursementDaoImpl();
    private static final ReimbursementService service = new ReimbursementServiceImpl(dao);
    private static User manager;
    private static User employee;

    /*
    User TEST_USER_1 is a manager inside the database
    User TEST_USER_2 is an employee inside the database
    We will use both for testing
    * */
    @BeforeAll
    public static void setup() {
        manager = new User();
        manager.setUserId(1);
        manager.setManager(true);
        manager.setUsername("TEST_USER_1"); // this is the important part, it's what the query searches for in db

        employee = new User();
        employee.setUsername("TEST_USER_2");
        employee.setUserId(2);
        employee.setManager(false);
    }

    @Test
    @Order(1)
    void get_user() {
        User user = service.getUser(employee);
        Assertions.assertEquals(employee.getUserId(), user.getUserId());
        Assertions.assertEquals(employee.getUsername(), user.getUsername());
        Assertions.assertEquals(employee.isManager(), user.isManager());
        Assertions.assertNotNull(user.getMyExpenses());
        Assertions.assertEquals(3, user.getMyExpenses().size());
        employee = user;

        user = service.getUser(manager);
        Assertions.assertEquals(manager.getUserId(), user.getUserId());
        Assertions.assertEquals(manager.getUsername(), user.getUsername());
        Assertions.assertEquals(manager.isManager(), user.isManager());
        Assertions.assertNotNull(user.getMyExpenses());
        Assertions.assertEquals(1, user.getMyExpenses().size());
        manager = user;
    }

    @Test
    @Order(2)
    void get_all_expenses() {
        try {
            HashSet<Expense> expenses = service.getAllExpenses(manager);
            Assertions.assertEquals(4, expenses.size());
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    void get_all_expenses_illegal_access_exception() {
        IllegalAccessException e = Assertions.assertThrows(IllegalAccessException.class, () -> service.getAllExpenses(employee));
        System.out.println(e.getMessage());
    }

    @Test
    @Order(4)
    void get_expense() {
        Expense e = employee.getMyExpenses().iterator().next();
        try {
            Expense expense = service.getExpense(employee, e.getExpenseId());
            Assertions.assertNotNull(expense);
            Assertions.assertEquals(e.getUserId(), expense.getUserId());
            Assertions.assertEquals(e.getStatus(), expense.getStatus());
            Assertions.assertEquals(e.getAmountInCents(), expense.getAmountInCents());
            Assertions.assertEquals(e.getDateSubmitted(), expense.getDateSubmitted());
            Assertions.assertEquals(e.getReasonSubmitted(), expense.getReasonSubmitted());
            Assertions.assertEquals(e.getFileURL(), expense.getFileURL());
        } catch (IllegalAccessException i) {
            fail(i.getMessage());
        }
    }

    @Test
    @Order(5)
    void get_expense_2() {
        Expense e = employee.getMyExpenses().iterator().next();
        try {
            Expense expense = service.getExpense(manager, e.getExpenseId()); // should work with manager as requester
            Assertions.assertNotNull(expense);
            Assertions.assertEquals(e.getUserId(), expense.getUserId());
            Assertions.assertEquals(e.getStatus(), expense.getStatus());
            Assertions.assertEquals(e.getAmountInCents(), expense.getAmountInCents());
            Assertions.assertEquals(e.getDateSubmitted(), expense.getDateSubmitted());
            Assertions.assertEquals(e.getReasonSubmitted(), expense.getReasonSubmitted());
            Assertions.assertEquals(e.getFileURL(), expense.getFileURL());
        } catch (IllegalAccessException i) {
            fail(i.getMessage());
        }
    }

    @Test
    @Order(6)
    void get_expense_3() { // employee cannot get other users expenses
        Expense e = manager.getMyExpenses().iterator().next();
        IllegalAccessException i = Assertions.assertThrows(IllegalAccessException.class, () -> service.getExpense(employee, e.getExpenseId()));
        System.out.println(i.getMessage());
    }

    @Test
    @Order(7)
    void update_expense() {
        Expense e = employee.getMyExpenses().iterator().next();
        try {
            int amount = e.getAmountInCents();
            e.setAmountInCents(e.getAmountInCents()+500);
            service.updateExpense(employee, e);
            Expense expense = service.getExpense(employee, e.getExpenseId());
            Assertions.assertNotNull(expense);
            Assertions.assertEquals(amount + 500,expense.getAmountInCents());

        } catch (IllegalAccessException i) {
            fail(i.getMessage());
        }
    }

    @Test
    @Order(8)
    void update_expense_2() {
        Expense e = employee.getMyExpenses().iterator().next();
        try {
            int amount = e.getAmountInCents();
            e.setAmountInCents(e.getAmountInCents()-500);
            service.updateExpense(manager, e); // should work with manager as requester
            Expense expense = service.getExpense(employee, e.getExpenseId());
            Assertions.assertNotNull(expense);
            Assertions.assertEquals(amount - 500,expense.getAmountInCents());

        } catch (IllegalAccessException i) {
            fail(i.getMessage());
        }
    }

    @Test
    @Order(9)
    void update_expense_3() {
        Expense e = manager.getMyExpenses().iterator().next();
        Assertions.assertNotEquals(e.getUserId(), employee.getUserId());
        IllegalAccessException i = Assertions.assertThrows(IllegalAccessException.class, () -> service.updateExpense(employee, e));
    }

    @Test
    @Order(10)
    void create_expense() {
        Expense expense = new Expense();
        expense.setAmountInCents(300);
        expense.setDateSubmitted(System.currentTimeMillis()/1000L);
        expense.setReasonSubmitted("SERVICE TEST REASON");
        Expense e = service.createExpense(employee, expense);
        Assertions.assertNotEquals(0, e.getExpenseId());
    }
}
