package utils;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {
    static Logger logger = Logger.getLogger(ConnectionUtil.class.getName());

    public static Connection createConnection() throws SQLException{
            // "jdbc:postgresql://35.203.25.121:5432/ReimbursementDB?user=user&password=password"
            // Not storing the login credentials in the code
            return DriverManager.getConnection(System.getenv("P1_DB_ACCESS"));

    }
}
