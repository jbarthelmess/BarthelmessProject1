package daos;

import entities.Expense;
import entities.LoginAttempt;
import entities.User;

import java.util.Set;

public interface ReimbursementDAO {
    /** Creates an expense
    * Expense ought to contain:
    *   - an amountInCents
    *   - a userId
    *   - a reason for submission
    *   - a fileURL (optional)
    *
    * returns null if missing these, otherwise returns the expense with a new expense Id number*/
    Expense createExpense(Expense expense);

    /** Retrieves User information with the expenses attached
     * At minimum, expenses in the user expense set ought to contain
     *  - expense ID
     *  - amount
     *  - status
     *  - date submitted
     *
     *  returns null if invalid user is given*/
    User getUser(User user);

    /** Retrieves All the information for a given expense
     *
     * returns null if expense Id is invalid*/
    Expense getExpense(int expenseId);

    /** Retrieves basic information for all expenses
     * At minimum, expenses should contain
     *  - expense ID
     *  - amount
     *  - status
     *  - date submitted*/
    Set<Expense> getAllExpenses();

    /** Checks for valid user login credentials
     * returns User object with:
     *  - username
     *  - userId
     *  - Manager status
     *
     * returns null if login credentials are invalid or if there is missing info*/
    User checkLogin(LoginAttempt loginAttempt);

    /** Updates expense in with new information
     * updatable fields include
     *    - status
     *    - dateResolved
     *    - reasonResolved
     *    - reasonSubmitted
     *    - fileURL
     *    - managerHandler
     *    - amount
     *
     * returns updated expense or null if no such expense exists in the database*/
    Expense updateExpense(Expense expense);

    // For min spec there will be no delete methods
}
