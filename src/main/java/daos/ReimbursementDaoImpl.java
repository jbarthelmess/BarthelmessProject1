package daos;

import entities.Expense;
import entities.User;
import org.apache.log4j.Logger;
import utils.ConnectionUtil;

import java.sql.*;
import java.util.HashSet;

public class ReimbursementDaoImpl implements ReimbursementDAO{
    static Logger logger = Logger.getLogger(ReimbursementDaoImpl.class.getName());

    @Override
    public Expense createExpense(Expense expense) {
        try(Connection conn = ConnectionUtil.createConnection()) {
            String query = "insert into expense (user_id, amount_cents, reason_submitted, date_submitted, status) values (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, expense.getUserId());
            ps.setInt(2, expense.getAmountInCents());
            ps.setString(3, expense.getReasonSubmitted());
            ps.setLong(4, expense.getDateSubmitted());
            ps.setString(5, expense.getStatusAsString());
            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            expense.setExpenseId(rs.getInt("expense_id"));
            return expense;
        } catch (SQLException s) {
            logger.error(s.getMessage());
            return null;
        }
    }

    @Override
    public User getUser(User user) {
        try(Connection conn = ConnectionUtil.createConnection()) {
            String query = "select * from users natural join expense where username = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,user.getUsername());
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Expense expense = new Expense();
                expense.setExpenseId(rs.getInt("expense_id"));
                expense.setDateSubmitted(rs.getLong("dateSubmitted"));
                expense.setReasonSubmitted(rs.getString("reason_submitted"));
                expense.setStatusFromString(rs.getString("status"));
            }

            return user;
        } catch (SQLException s) {
            logger.error(s.getMessage());
            return null;
        }
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
