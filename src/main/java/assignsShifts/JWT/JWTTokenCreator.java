package assignsShifts.JWT;

import assignsShifts.entities.user.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JWTTokenCreator {
  @Autowired private Algorithm algorithm;

  @Value("${JWT.token}")
  private String JWTToken;

  @Autowired private Gson gson;

  public String createJWTToken(User user) {
    return JWT.create()
        .withIssuer(JWTToken)
        .withSubject(JWTToken + " Details")
        .withClaim("user", gson.toJson(user))
        .withIssuedAt(new Date(System.currentTimeMillis()))
        .withExpiresAt(new Date(System.currentTimeMillis() + (86400L * 10000L)))
        .withJWTId(UUID.randomUUID().toString())
        .withNotBefore(new Date(System.currentTimeMillis()))
        .sign(algorithm);
  }
}
