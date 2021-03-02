package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import entities.User;
import org.apache.log4j.Logger;

/*
JWT FORMAT -- JWT's will have 3 claims
    - userID
    - username
    - isManager
* */
public class JwtUtil {
    private static final String secret = System.getenv("JWT");
    private static final Algorithm algo = Algorithm.HMAC512(secret);
    static Logger logger = Logger.getLogger(JwtUtil.class.getName());

    public static String generate(User user) {
        return JWT.create().withClaim("userId",user.getUserId()).withClaim("username", user.getUsername()).withClaim("isManager", user.isManager()).sign(algo);
    }

    public static DecodedJWT isValidJWT(String token) {
        try {
            DecodedJWT jwt = JWT.require(algo).withClaimPresence("userId").withClaimPresence("username").withClaimPresence("isManager").build().verify(token);
            return jwt;
        } catch(JWTVerificationException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
