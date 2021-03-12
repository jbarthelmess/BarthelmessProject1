package services;

import entities.Expense;
import entities.LoginAttempt;
import entities.ManagerStatistics;
import entities.User;
import io.javalin.http.Context;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public interface ReimbursementService {
    /** Checks for login credentials
     *
     * throws an error if username or password is null or empty string*/
    User login(LoginAttempt loginAttempt) throws IllegalAccessException;

    /** Retrieves user info*/
    User getUser(User user);

    /** Retrieves expense with a given expenseId
     *
     * throws an exception if the user is not permitted to view the expense
     * returns the expense, or null, if the expense cannot be found*/
    Expense getExpense(User user, int expenseId) throws IllegalAccessException;

    /** Retrieves all expenses
     *
     * Should only be able to be used by managers
     * throws an exception if the user is not a manager*/
    Set<Expense> getAllExpenses(User user) throws IllegalAccessException;

    /** Creates a new expense
     *
     * new expenses should have an amount, reason, and an optional fileURL
     * dateSubmitted and userId will be set automatically
     *
     * throws an exception if the reason and amount are not present
     * returns the newly created expense otherwise*/
    Expense createExpense(User user, Expense expense);

    /** Updates an existing expense
     *
     * the most complicated method, should enforce updating rules based on user privileges and expense status
     * Rules:
     *      - expenses can only be updated if they are pending
     *      - users can only update fields they submit (amount, reason, file)
     *      - managers cannot resolve their own expenses
     *      - managers can only update certain fields and must update status if they are updating(status, reason resolved)
     *      - auto-generated fields (expense Id, user Id, dateSubmitted, dateResolved) cannot be updated
     *
     * Throws an exception if:
     *      - anyone attempts to update a resolved expense
     *      - anyone attempts to update a non-existent expense
     *      - users attempt to update expenses they do not have access to
     *      - managers attempt to update an expense without resolving it
     *
     * Returns the updated expense*/
    Expense updateExpense(User user, Expense expense) throws IllegalAccessException;

    /** Gets manager statistics*/
    ManagerStatistics getManagerStatistics(User user) throws IllegalAccessException;

    /** Uploads a file to google cloud bucket*/
    String uploadFile(File file, User user) throws IOException;
}
