package utilTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.ConnectionUtil;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.fail;

public class ConnectionUtilTests {
    @Test
    void generate_connection() {
        try {
            Connection conn = ConnectionUtil.createConnection();
            Assertions.assertNotNull(conn);
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }
}
