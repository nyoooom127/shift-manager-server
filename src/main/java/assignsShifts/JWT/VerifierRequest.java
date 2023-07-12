package assignsShifts.JWT;

import assignsShifts.models.enums.UserPermissionsEnum;
import assignsShifts.repository.UserRepository;
import com.auth0.jwt.JWTVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifierRequest {
  @Autowired private JWTVerifier jwtVerifier;
  @Autowired private UserRepository userRepository;

  public boolean isVerify(String token, UserPermissionsEnum userPermissionsEnum) {
    try {
      String userId = jwtVerifier.verify(token).getClaim("userId").asString();

      return userRepository.isVerify(userId, userPermissionsEnum);
    } catch (Exception exception) {
      return false;
    }
  }
}
