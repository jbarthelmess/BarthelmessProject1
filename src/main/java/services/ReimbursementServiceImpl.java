package services;

import daos.ReimbursementDAO;
import entities.Expense;
import entities.ExpenseStatus;
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
        expense.setDateSubmitted(System.currentTimeMillis()/1000L);
        expense.setUserId(user.getUserId());
        if(expense.getAmountInCents()<=0) throw new IllegalArgumentException("Amount should not be less than or equal to zero");
        if(expense.getReasonSubmitted() == null || expense.getReasonSubmitted().equals(""))
            throw new IllegalArgumentException("Reason should not be empty or null");
        if(expense.getFileURL() != null && expense.getFileURL().equals("")) expense.setFileURL(null);
        return dao.createExpense(expense);
    }

    @Override
    public Expense updateExpense(User user, Expense expense) throws IllegalAccessException{
        Expense e = dao.getExpense(expense.getExpenseId());
        if(e == null){
            logger.warn("User "+ user.getUsername() + " attempted to update non-existent expense "+ expense.getExpenseId()+".");
            throw new NullPointerException("Could not find expense");
        }

        // first, prevent people from updating a completed reimbursement request
        if(e.getStatus() != ExpenseStatus.PENDING) {
            logger.warn("User "+ user.getUsername() + " attempted to illegally update expense "+ expense.getExpenseId()+".");
            throw new IllegalAccessException("Cannot update a completed expense");
        }

        // next enforce what can be done by non-managers
        if(!user.isManager()) {
            // Non-managers cannot update other people's expenses
            if(user.getUserId() != e.getUserId()) {
                logger.warn("User "+ user.getUsername() + " attempted to illegally update expense "+ expense.getExpenseId()+".");
                throw new IllegalAccessException("User is not permitted to update the indicated expense");
            }
            // set parameters that non-managers cannot change
            expense.setStatus(ExpenseStatus.PENDING);
            expense.setManagerHandler(0);
            expense.setReasonResolved(null);
            expense.setDateResolved(0);
            return dao.updateExpense(expense);
        }
        expense.setDateResolved(System.currentTimeMillis()/1000L);
        expense.setManagerHandler(user.getUserId());
        // finally perform some checking from the manager side
        if(expense.getStatus() == ExpenseStatus.PENDING) {
            // if the manager is not resolving the expense, maintain these parameters as is
            expense.setManagerHandler(0);
            expense.setReasonResolved(null);
            expense.setDateResolved(0);
        }
        return dao.updateExpense(expense);
    }

    @Override
    public User login(LoginAttempt loginAttempt) {
        return dao.checkLogin(loginAttempt);
    }
}
