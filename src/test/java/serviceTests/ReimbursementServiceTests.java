package serviceTests;

import daos.ReimbursementDAO;
import daos.ReimbursementDaoImpl;
import entities.Expense;
import entities.ExpenseStatus;
import entities.ManagerStatistics;
import entities.User;
import org.junit.jupiter.api.*;
import services.ReimbursementService;
import services.ReimbursementServiceImpl;

import java.util.Set;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReimbursementServiceTests {
    private static final ReimbursementDAO dao = new ReimbursementDaoImpl();

    private static final ReimbursementService service = new ReimbursementServiceImpl(dao);
    private static User manager;
    private static User employee;
    private static Expense createdExpense;

    /*
    User Andrew Wiggin is an employee inside the database
    User Peter Wiggin is a manager inside the database
    We will use both for testing
    * */
    @BeforeAll
    public static void setup() {
        manager = new User();
        manager.setUserId(2);
        manager.setManager(true);
        manager.setUsername("Peter Wiggin"); // this is the important part, it's what the query searches for in db

        employee = new User();
        employee.setUsername("Andrew Wiggin");
        employee.setUserId(1);
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
        Assertions.assertNotEquals(0, user.getMyExpenses().size());
        employee = user;

        user = service.getUser(manager);
        Assertions.assertEquals(manager.getUserId(), user.getUserId());
        Assertions.assertEquals(manager.getUsername(), user.getUsername());
        Assertions.assertEquals(manager.isManager(), user.isManager());
        Assertions.assertNotNull(user.getMyExpenses());
        Assertions.assertNotEquals(0, user.getMyExpenses().size());
        manager = user;
    }

    @Test
    @Order(2)
    void get_all_expenses() {
        try {
            Set<Expense> expenses = service.getAllExpenses(manager);
            Assertions.assertNotEquals(0, expenses.size());
        } catch (IllegalAccessException e) {
            Assertions.fail(e.getMessage());
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
            Assertions.fail(i.getMessage());
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
            Assertions.fail(i.getMessage());
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
    void create_expense() {
        createdExpense = new Expense();
        createdExpense.setAmountInCents(800);
        createdExpense.setReasonSubmitted("SERVICE TEST REASON");
        Expense e = service.createExpense(employee, createdExpense);
        Assertions.assertNotEquals(0, e.getExpenseId());
        Assertions.assertNotEquals(0, e.getDateSubmitted());
        Assertions.assertEquals(ExpenseStatus.PENDING, e.getStatus());
        Assertions.assertEquals(employee.getUserId(), e.getUserId());
    }

    @Test
    @Order(8)
    void update_expense() {
        try {
            int amount = createdExpense.getAmountInCents();
            createdExpense.setAmountInCents(createdExpense.getAmountInCents()+500);
            service.updateExpense(employee, createdExpense);
            Expense updatedExpense = service.getExpense(employee, createdExpense.getExpenseId());
            Assertions.assertNotNull(updatedExpense);
            Assertions.assertEquals(amount + 500,updatedExpense.getAmountInCents());
        } catch (IllegalAccessException i) {
            Assertions.fail(i.getMessage());
        }
    }

    @Test
    @Order(9)
    void update_expense_2() {
        createdExpense.setAmountInCents(createdExpense.getAmountInCents()-500);
        // managers cannot update employees expenses without setting the status
        IllegalAccessException i = Assertions.assertThrows(IllegalAccessException.class, () -> service.updateExpense(manager, createdExpense));
        System.out.println(i.getMessage());
        createdExpense.setAmountInCents(createdExpense.getAmountInCents()+500);
    }

    @Test
    @Order(10)
    void update_expense_3() {
        Expense e = manager.getMyExpenses().iterator().next();
        // employees cannot update other's expenses
        Assertions.assertNotEquals(e.getUserId(), employee.getUserId());
        Assertions.assertThrows(IllegalAccessException.class, () -> service.updateExpense(employee, e));
    }

    @Test
    @Order(11)
    void update_expense_4() {
        // employees cannot approve expenses, will go through but approved field will not persist
        createdExpense.setStatus(ExpenseStatus.APPROVED);
        try {
            Expense e = service.updateExpense(employee, createdExpense);
            Assertions.assertEquals(ExpenseStatus.PENDING, e.getStatus());
            createdExpense.setStatus(ExpenseStatus.PENDING);
        } catch (IllegalAccessException i) {
            Assertions.fail(i.getMessage());
        }
    }

    @Test
    @Order(12)
    void update_expense_5() {
        createdExpense.setStatus(ExpenseStatus.DENIED);
        createdExpense.setReasonResolved("SERVICE TEST REASON RESOLVED");
        try {
            Expense expense = service.updateExpense(manager, createdExpense);
            Assertions.assertEquals("SERVICE TEST REASON RESOLVED", expense.getReasonResolved());
            Assertions.assertNotEquals(0,expense.getDateResolved());
            Assertions.assertEquals(ExpenseStatus.DENIED, expense.getStatus());
        } catch(IllegalAccessException i) {
            Assertions.fail(i.getMessage());
        }
    }

    @Test
    @Order(13)
    void get_manager_statistics() {
        try {
            ManagerStatistics stats = service.getManagerStatistics(manager);
            Assertions.assertEquals(stats.getUserId(), manager.getUserId());
            Assertions.assertTrue(stats.getDeniedCount() > 0);
            System.out.println(stats);
        } catch (IllegalAccessException i) {
            Assertions.fail("manager should have access to statistics");
        }
    }

    @Test
    @Order(14)
    void get_manager_statistics_as_employee() {
        Assertions.assertThrows(IllegalAccessException.class, ()-> service.getManagerStatistics(employee));
    }
}
