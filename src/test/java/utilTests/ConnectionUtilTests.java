package utilTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.ConnectionUtil;

import java.sql.Connection;

public class ConnectionUtilTests {
    @Test
    void generate_connection() {
        Connection conn = ConnectionUtil.createConnection();
        Assertions.assertNotNull(conn);
    }
}
