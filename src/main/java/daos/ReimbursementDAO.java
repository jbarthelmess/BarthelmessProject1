package daos;

import entities.Expense;
import entities.User;

import java.util.HashSet;

public interface ReimbursementDAO {
    // Create, don't need to create users
    Expense createExpense(Expense expense);

    // Read
    User getUser(User user);
    Expense getExpense(int expenseId);
    HashSet<Expense> getAllExpenses();

    // Update
    Expense updateExpense(Expense expense);

    // For min spec there will be no delete methods
}
