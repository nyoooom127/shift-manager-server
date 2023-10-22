package assignsShifts.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.security.KeyPairGenerator;

@Configuration
public class TWTConfiguration {
//  @Value("${auth.key.path}")
//  private Resource keyResource;

  @Value("${JWT.token}")
  private String JWTToken;

//  KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//
//  @Bean
//  public String getKey()

  @Bean
  public Algorithm getAlgorithm() {
    return Algorithm.HMAC256(JWTToken);
  }

  @Bean
  public JWTVerifier getJwtVerifier(@Autowired Algorithm algorithm) {
    return JWT.require(algorithm).withIssuer(JWTToken).build();
  }
}
