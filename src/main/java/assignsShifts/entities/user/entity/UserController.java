package assignsShifts.entities.user.entity;

import assignsShifts.JWT.VerifierRequest;
import assignsShifts.models.enums.UserPermissionsEnum;
import com.mongodb.client.result.DeleteResult;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired private UserService userService;
  @Autowired private VerifierRequest verifierRequest;

  @GetMapping
  public ResponseEntity<List<User>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.userService.findAll().stream().map(User::hideAuthData).toList());
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    Optional<String> optionalTokenString =
        this.userService.logIn(loginRequest.getUserName(), loginRequest.getPassword());

    if (optionalTokenString.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalTokenString.get());
  }

  @PostMapping(value = "/create")
  public ResponseEntity<User> createUser(
      @RequestBody User user, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<User> optionalUser = this.userService.create(user);

    if (optionalUser.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalUser.get().hideAuthData());
  }

  @PostMapping(value = "/update")
  public ResponseEntity<User> updateUser(
      @RequestBody User user, @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.USER, user.getId())) {
      return ResponseEntity.ok().build();
    }

    Optional<User> optionalUser = this.userService.update(user);

    if (optionalUser.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalUser.get().hideAuthData());
  }

  @DeleteMapping
  public ResponseEntity<DeleteResult> deleteUser(
      @RequestParam String id, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<DeleteResult> optionalDeleteResult = userService.delete(id);

    if (optionalDeleteResult.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalDeleteResult.get());
  }

  @Getter
  public static class LoginRequest {
    private String userName;
    private String password;
  }
}
