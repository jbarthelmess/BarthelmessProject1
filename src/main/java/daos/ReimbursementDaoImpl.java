package daos;

import entities.Expense;
import entities.LoginAttempt;
import entities.User;
import org.apache.log4j.Logger;
import utils.ConnectionUtil;

import java.sql.*;
import java.util.HashSet;

import static java.sql.Types.NULL;

public class ReimbursementDaoImpl implements ReimbursementDAO{
    static Logger logger = Logger.getLogger(ReimbursementDaoImpl.class.getName());

    @Override
    public Expense createExpense(Expense expense) {
        try(Connection conn = ConnectionUtil.createConnection()) {
            String query = "insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted) values (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, expense.getUserId());
            ps.setInt(2, expense.getAmountInCents());
            ps.setString(3, expense.getReasonSubmitted());
            ps.setLong(4, expense.getDateSubmitted());
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

            HashSet<Expense> expenses = new HashSet<>();
            while(rs.next()) {
                Expense expense = new Expense();
                expense.setExpenseId(rs.getInt("expense_id"));
                expense.setDateSubmitted(rs.getLong("dateSubmitted"));
                expense.setReasonSubmitted(rs.getString("reason_submitted"));
                expense.setStatusFromString(rs.getString("status"));
                expense.setDateResolved(rs.getLong("dateResolved"));
                expense.setReasonResolved(rs.getString("reason_resolved"));
                expense.setAmountInCents(rs.getInt("amount_cents"));
                expense.setUserId(user.getUserId());
                expense.setManagerHandler(rs.getInt("manager_handler"));
                expense.setFileURL(rs.getString("file_url"));
                expenses.add(expense);
            }
            user.setMyExpenses(expenses);
            return user;
        } catch (SQLException s) {
            logger.error(s.getMessage());
            return null;
        }
    }

    @Override
    public Expense getExpense(int expenseId) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "select * from expense where expense_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, expenseId);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) return null;
            Expense expense = new Expense();
            expense.setExpenseId(rs.getInt("expense_id"));
            expense.setDateSubmitted(rs.getLong("dateSubmitted"));
            expense.setReasonSubmitted(rs.getString("reason_submitted"));
            expense.setStatusFromString(rs.getString("status"));
            expense.setDateResolved(rs.getLong("dateResolved"));
            expense.setReasonResolved(rs.getString("reason_resolved"));
            expense.setAmountInCents(rs.getInt("amount_cents"));
            expense.setUserId(rs.getInt("user_id"));
            expense.setManagerHandler(rs.getInt("manager_handler"));
            expense.setFileURL(rs.getString("file_url"));
            return expense;
        } catch (SQLException s) {
            logger.error(s.getMessage());
            return null;
        }
    }

    @Override
    public HashSet<Expense> getAllExpenses() {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "select * from expense";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            HashSet<Expense> expenses = new HashSet<>();
            while(rs.next()) {
                Expense expense = new Expense();
                expense.setExpenseId(rs.getInt("expense_id"));
                expense.setDateSubmitted(rs.getLong("dateSubmitted"));
                expense.setReasonSubmitted(rs.getString("reason_submitted"));
                expense.setStatusFromString(rs.getString("status"));
                expense.setDateResolved(rs.getLong("dateResolved"));
                expense.setReasonResolved(rs.getString("reason_resolved"));
                expense.setAmountInCents(rs.getInt("amount_cents"));
                expense.setUserId(rs.getInt("user_id"));
                expense.setManagerHandler(rs.getInt("manager_handler"));
                expense.setFileURL(rs.getString("file_url"));
                expenses.add(expense);
            }
            return expenses;
        } catch (SQLException s) {
            logger.error(s.getMessage());
            return null;
        }
    }

    @Override
    public Expense updateExpense(Expense expense) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "update expense set manager_handler = ?, amount_cents = ?, reason_submitted = ?, reason_resolved = ?, dateResolved = ?, status = ?, file_url = ? where expense_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            if(expense.getManagerHandler() == 0) {
                ps.setNull(1, NULL);
            } else {
                ps.setInt(1, expense.getManagerHandler());
            }
            ps.setInt(2,expense.getAmountInCents());
            ps.setString(3, expense.getReasonSubmitted());
            if(expense.getDateResolved() <= 0) {
                ps.setNull(4, NULL);
                ps.setNull(5, Types.BIGINT);
            } else {
                ps.setString(4, expense.getReasonResolved());
                ps.setLong(5,expense.getDateResolved());
            }
            ps.setString(6,expense.getStatusAsString());
            ps.setString(7, expense.getFileURL());
            ps.setInt(8, expense.getExpenseId());
            int success = ps.executeUpdate();
            return success > 0? expense: null;
        } catch (SQLException s) {
            logger.error(s.getMessage());
            return null;
        }
    }

    public User checkLogin(LoginAttempt loginAttempt) {
        try (Connection conn = ConnectionUtil.createConnection()) {
            String query = "select username, user_id, is_manager from users where username = ? and pass_word = ?";
            // check username and password combo, should return zero rows if either is wrong
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, loginAttempt.getUsername());
            ps.setString(2, loginAttempt.getPassword());
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) return null;
            User user = new User();
            user.setUsername(rs.getString("username"));
            user.setUserId(rs.getInt("user_id"));
            user.setManager(rs.getBoolean("is_manager"));
            return user;
        } catch (SQLException s) {
            logger.error(s.getMessage());
            return null;
        }
    }
}
