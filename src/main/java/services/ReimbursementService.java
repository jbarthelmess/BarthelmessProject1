package services;

import entities.Expense;
import entities.User;

import java.util.HashSet;

public interface ReimbursementService {
    User getUser(User user);
    Expense getExpense(User user, int expenseId) throws IllegalAccessException;
    HashSet<Expense> getAllExpenses(User user) throws IllegalAccessException;

    Expense createExpense(User user, Expense expense);

    Expense updateExpense(User user, Expense expense) throws IllegalAccessException;
}
