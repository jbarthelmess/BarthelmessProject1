package utilTests;

import com.auth0.jwt.interfaces.DecodedJWT;
import entities.User;
import org.junit.jupiter.api.*;
import utils.JwtUtil;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JwtUtilTests {
    private static String jwt;
    private static User user;

    @Test
    @Order(1)
    void generate_jwt() {
        user = new User();
        user.setUserId(1);
        user.setManager(true);
        user.setUsername("TEST_USER_1");
        jwt = JwtUtil.generate(user);
        System.out.println(jwt);
    }

    @Test
    @Order(2)
    void decoded_jwt() {
        DecodedJWT decodedJWT = JwtUtil.isValidJWT(jwt);
        Assertions.assertEquals(user.getUsername(),decodedJWT.getClaim("username").asString());
        Assertions.assertEquals(user.getUserId(), decodedJWT.getClaim("userId").asInt());
        Assertions.assertEquals(user.isManager(), decodedJWT.getClaim("isManager").asBoolean());
    }
}
