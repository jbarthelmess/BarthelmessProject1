package services;

import daos.ReimbursementDAO;
import entities.Expense;
import entities.LoginAttempt;
import entities.User;
import org.apache.log4j.Logger;

import java.util.HashSet;

public class ReimbursementServiceImpl implements ReimbursementService{
    private final ReimbursementDAO dao;
    static Logger logger = Logger.getLogger(ReimbursementServiceImpl.class.getName());

    public ReimbursementServiceImpl(ReimbursementDAO dao) {
        if(dao == null) {
            throw new NullPointerException("ReimbursementDAO cannot be null");
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
        if(expense == null) return null;
        if(user.isManager() || user.getUserId() == expense.getUserId()){
            return expense;
        }
        logger.warn("User "+ user.getUsername() + " attempted to illegally access expense "+ expenseId+".");
        throw new IllegalAccessException("User is not permitted to access the indicated expense");
    }

    @Override
    public HashSet<Expense> getAllExpenses(User user) throws IllegalAccessException{
        if(!user.isManager()) {
            logger.warn("User "+ user.getUsername() + " attempted to illegally access all expenses.");
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
        if(user.isManager() || user.getUserId() == e.getUserId()) {
            return dao.updateExpense(expense);
        }
        logger.warn("User "+ user.getUsername() + " attempted to illegally update expense "+ expense.getExpenseId()+".");
        throw new IllegalAccessException("User is not permitted to update the indicated expense");
    }

    @Override
    public boolean login(LoginAttempt loginAttempt) {
        return false;
    }
}
