package daoTests;

import daos.ReimbursementDAO;
import daos.ReimbursementDaoImpl;
import entities.Expense;
import entities.ExpenseStatus;
import entities.ManagerStatistics;
import entities.User;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReimbursementDaoTests {
    private static final ReimbursementDAO dao = new ReimbursementDaoImpl();
    private static User user;
    private static int expenseId;

    @BeforeAll
    public static void setup() {
        /*
        Database has 8 users: (-M means manager)
            1. Andrew Wiggin
            2. Peter Wiggin - M
            3. Valentine Wiggin
            4. Hyrum Graff - M
            5. Mazer Rackham - M
            6. Anderson - M
            7. Petra Arkanian
            8. Julian Delphiki
        Database has 12 expenses:
            Each Manager has 1, Each employee has 2
            All are pending

        We will be working with Andrew Wiggin for now
        */
        user = new User();
        user.setUsername("Andrew Wiggin");
        user.setUserId(1);
        user.setManager(false);
    }

    @Test
    @Order(1)
    void get_user() {
        user = dao.getUser(user);
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getMyExpenses());
        Assertions.assertNotEquals(0, user.getMyExpenses().size());
    }

    @Test
    @Order(2)
    void create_expense() {
        int size = user.getMyExpenses().size();
        Expense expense = new Expense();
        expense.setAmountInCents(1);
        expense.setDateSubmitted(System.currentTimeMillis()/1000L);
        expense.setUserId(user.getUserId());
        expense.setStatusFromString("PENDING");
        expense.setReasonSubmitted("DAO TEST EXPENSE REASON");
        // create the expense
        Expense createdExpense = dao.createExpense(expense);
        Assertions.assertNotNull(createdExpense);
        Assertions.assertNotEquals(0, createdExpense.getExpenseId());
        user = dao.getUser(user);
        Assertions.assertEquals(size+1, user.getMyExpenses().size());
        boolean foundInUserList = false;
        for(Expense e : user.getMyExpenses()) {
            if(e.getExpenseId() == createdExpense.getExpenseId()) {
                expenseId = e.getExpenseId();
                foundInUserList = true;
                break;
            }
        }
        if(!foundInUserList) fail("Expense was not found in userList");
    }

    @Test
    @Order(3)
    void get_expense() {
        // get expense that was just created
        Expense expense = dao.getExpense(expenseId);
        Assertions.assertNotNull(expense);
        Assertions.assertEquals(expenseId, expense.getExpenseId());
        Assertions.assertEquals(user.getUserId(), expense.getUserId());
        Assertions.assertEquals(ExpenseStatus.PENDING, expense.getStatus());
        Assertions.assertNotNull(expense.getReasonSubmitted());
        Assertions.assertNotEquals("", expense.getReasonSubmitted());
        Assertions.assertTrue(expense.getAmountInCents() >= 0);
    }

    @Test
    @Order(4)
    void get_all_expenses() {
        Set<Expense> expenses = dao.getAllExpenses();
        Assertions.assertNotNull(expenses);
        Assertions.assertNotEquals(0, expenses.size());
        for(Expense expense : expenses) {
            Assertions.assertNotNull(expense);
            Assertions.assertNotEquals(0, expense.getExpenseId());
            Assertions.assertNotEquals(0, expense.getUserId());
            Assertions.assertNotNull(expense.getStatus());
            Assertions.assertNotNull(expense.getReasonSubmitted());
            Assertions.assertNotEquals("", expense.getReasonSubmitted());
            Assertions.assertTrue(expense.getAmountInCents() >= 0);
        }
    }

    @Test
    @Order(5)
    void update_expense() {
        // update created expense with new info
        // This test will update fields that normal employees can update
        //      - Reason submitted
        //      - amount
        //      - file attachment
        Expense expense = dao.getExpense(expenseId);
        expense.setReasonSubmitted("UPDATED DAO TEST EXPENSE REASON");
        expense.setAmountInCents(4);
        expense.setFileURL("https://cdn.britannica.com/36/22536-004-9855C103/Flag-Union-of-Soviet-Socialist-Republics.jpg");
        dao.updateExpense(expense);

        Expense updatedExpense = dao.getExpense(expenseId);
        Assertions.assertNotNull(updatedExpense);
        Assertions.assertEquals(4, updatedExpense.getAmountInCents());
        Assertions.assertEquals("https://cdn.britannica.com/36/22536-004-9855C103/Flag-Union-of-Soviet-Socialist-Republics.jpg", updatedExpense.getFileURL());
        Assertions.assertEquals("UPDATED DAO TEST EXPENSE REASON", expense.getReasonSubmitted());
        Assertions.assertEquals(expense.getStatus(), updatedExpense.getStatus());
        Assertions.assertEquals(expense.getUserId(), updatedExpense.getUserId());
    }

    @Test
    @Order(6)
    void resolve_expense() {
        // This test will update fields that managers can update
        //      - Resolution status
        //      - Date resolved
        //      - reason Resolved
        //      - manager handler
        Expense expense = dao.getExpense(expenseId);
        expense.setReasonResolved("DAO TEST REASON RESOLVED");
        expense.setDateResolved(System.currentTimeMillis()/1000L);
        expense.setManagerHandler(2);
        expense.setStatus(ExpenseStatus.DENIED);
        dao.updateExpense(expense);

        Expense updatedExpense = dao.getExpense(expenseId);
        Assertions.assertNotNull(updatedExpense);
        Assertions.assertEquals(expense.getAmountInCents(), updatedExpense.getAmountInCents());
        Assertions.assertEquals(expense.getFileURL(), updatedExpense.getFileURL());
        Assertions.assertEquals(expense.getReasonSubmitted(), updatedExpense.getReasonSubmitted());
        Assertions.assertEquals(expense.getReasonResolved(), updatedExpense.getReasonResolved());
        Assertions.assertEquals(expense.getStatus(), updatedExpense.getStatus());
        Assertions.assertEquals(expense.getUserId(), updatedExpense.getUserId());
        Assertions.assertEquals(expense.getDateSubmitted(), updatedExpense.getDateSubmitted());
        Assertions.assertEquals(expense.getDateResolved(), updatedExpense.getDateResolved());
        Assertions.assertEquals(expense.getManagerHandler(), updatedExpense.getManagerHandler());
    }

    @Test
    @Order(7)
    void get_manager_stats() {
        User manager = new User();
        manager.setManager(true);
        manager.setUsername("Peter Wiggin");
        manager.setUserId(2);
        ManagerStatistics stats = dao.getManagerStatistics(manager);
        Assertions.assertEquals(manager.getUserId(), stats.getUserId());
        Assertions.assertTrue(stats.getDeniedCount() > 0);
        System.out.println(stats);
    }
}
