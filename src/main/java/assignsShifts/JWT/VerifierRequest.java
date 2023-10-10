package assignsShifts.JWT;

import assignsShifts.entities.user.entity.User;
import assignsShifts.entities.user.entity.UserRepository;
import assignsShifts.models.enums.UserPermissionsEnum;
import com.auth0.jwt.JWTVerifier;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerifierRequest {
  @Autowired private JWTVerifier jwtVerifier;
  @Autowired private UserRepository userRepository;
  @Autowired private Gson gson;

  public boolean isVerify(String token) {
    return isVerify(token, UserPermissionsEnum.USER);
  }

  public boolean isAdmin(String token) {
    return isVerify(token, UserPermissionsEnum.ADMIN);
  }

  public boolean isVerify(String token, UserPermissionsEnum userPermissionsEnum) {
    return isVerify(token, userPermissionsEnum, null);
  }

  public boolean isVerify(String token, UserPermissionsEnum userPermissionsEnum, String userId) {
    try {
      String json = jwtVerifier.verify(token).getClaim("user").asString();
      User user = gson.fromJson(json, User.class);

      return user != null
          && (user.getAuthorizationData().getUserPermissions().equals(UserPermissionsEnum.ADMIN)
              || user.getAuthorizationData().getUserPermissions().equals(userPermissionsEnum)
              || user.getId().equals(userId));
    } catch (Exception exception) {
      return false;
    }
  }
}
