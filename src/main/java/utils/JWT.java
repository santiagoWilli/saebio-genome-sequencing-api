package utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

public class JWT {
    private static final String SECRET = "$mB+fCrO.-XsOIk:|?OhZHR/%./%)YVpSvyq;qD~8E$r.O-;Vuds);$JJd|jny.";

    private JWT() {}

    public static String generate() throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return com.auth0.jwt.JWT.create()
                .sign(algorithm);
    }

    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = com.auth0.jwt.JWT.require(algorithm)
                    .acceptLeeway(3600*2) // 2 hours
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception){
            return false;
        }
    }
}
