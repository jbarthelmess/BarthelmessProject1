package daos;

import entities.Expense;
import entities.User;
import org.apache.log4j.Logger;

import java.util.HashSet;

public class ReimbursementDaoImpl implements ReimbursementDAO{
    static Logger logger = Logger.getLogger(ReimbursementDaoImpl.class.getName());

    @Override
    public Expense createExpense(Expense expense) {
        return null;
    }

    @Override
    public User getUser(String username) {
        return null;
    }

    @Override
    public Expense getExpense(int expenseId) {
        return null;
    }

    @Override
    public HashSet<Expense> getAllExpenses() {
        return null;
    }

    @Override
    public Expense updateExpense(Expense expense) {
        return null;
    }
}
