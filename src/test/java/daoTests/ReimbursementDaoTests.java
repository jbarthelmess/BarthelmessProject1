package daoTests;

import daos.ReimbursementDAO;
import daos.ReimbursementDaoImpl;
import entities.Expense;
import entities.ExpenseStatus;
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
        Database has 2 users:
            TEST_USER_1 who is a manager
            TEST_USER_2 who is not a manager
        Database has 4 expenses:
            1 belongs to TEST_USER_1
            3 belong to TEST_USER_2
            All are pending

        We will be working with TEST_USER_2's expenses, so we start by setting up her user info
        */
        user = new User();
        user.setUsername("TEST_USER_2");
        user.setUserId(2);
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
        // Expense ID's 1-4 are in the database
        Expense expense = dao.getExpense(2);
        Assertions.assertNotNull(expense);
        Assertions.assertEquals(2, expense.getExpenseId());
        Assertions.assertEquals(2, expense.getUserId());
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
        expense.setReasonSubmitted("UPDATED");
        expense.setAmountInCents(4);
        expense.setFileURL("https://cdn.britannica.com/36/22536-004-9855C103/Flag-Union-of-Soviet-Socialist-Republics.jpg");
        dao.updateExpense(expense);

        Expense updatedExpense = dao.getExpense(expenseId);
        Assertions.assertNotNull(updatedExpense);
        Assertions.assertEquals(4, updatedExpense.getAmountInCents());
        Assertions.assertEquals("https://cdn.britannica.com/36/22536-004-9855C103/Flag-Union-of-Soviet-Socialist-Republics.jpg", updatedExpense.getFileURL());
        Assertions.assertEquals("UPDATED", expense.getReasonSubmitted());
        Assertions.assertEquals(expense.getStatus(), updatedExpense.getStatus());
        Assertions.assertEquals(expense.getUserId(), updatedExpense.getUserId());

    }
}
