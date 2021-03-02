package services;

import entities.Expense;
import entities.LoginAttempt;
import entities.User;

import java.util.HashSet;

public interface ReimbursementService {
    User login(LoginAttempt loginAttempt);
    User getUser(User user);
    Expense getExpense(User user, int expenseId) throws IllegalAccessException;
    HashSet<Expense> getAllExpenses(User user) throws IllegalAccessException;

    Expense createExpense(User user, Expense expense);

    Expense updateExpense(User user, Expense expense) throws IllegalAccessException;
}
