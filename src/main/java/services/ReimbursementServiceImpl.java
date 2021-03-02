package services;

import daos.ReimbursementDAO;
import entities.Expense;
import entities.User;

import java.util.HashSet;

public class ReimbursementServiceImpl implements ReimbursementService{
    private final ReimbursementDAO dao;

    public ReimbursementServiceImpl(ReimbursementDAO dao) {
        if(dao == null) {
            throw new IllegalArgumentException("ReimbursementDAO cannot be null");
        }
        this.dao = dao;
    }

    @Override
    public User getUser(User user) {
        return dao.getUser(user);
    }

    @Override
    public Expense getExpense(User user, int expenseId) throws IllegalAccessException {
        Expense expense = dao.getExpense(expenseId);
        if(user.isManager() || user.getUserId() == expense.getUserId()){
            return expense;
        }
        throw new IllegalAccessException("User is not permitted to access the indicated expense");
    }

    @Override
    public HashSet<Expense> getAllExpenses(User user) throws IllegalAccessException{
        if(!user.isManager()) {
            throw new IllegalAccessException("User is not permitted to access all expenses");
        }
        return dao.getAllExpenses();
    }

    @Override
    public Expense createExpense(User user, Expense expense) {
        expense.setUserId(user.getUserId());
        return dao.createExpense(expense);
    }

    @Override
    public Expense updateExpense(User user, Expense expense) throws IllegalAccessException{
        Expense e = dao.getExpense(expense.getExpenseId());
        if(user.isManager() || user.getUserId() == expense.getUserId()) {
            return dao.updateExpense(expense);
        }
        throw new IllegalAccessException("User is not permitted to update the indicated expense");
    }
}
