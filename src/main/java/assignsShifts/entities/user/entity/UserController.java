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

  @CrossOrigin
  @GetMapping
  public ResponseEntity<List<User>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.userService.findAll().stream().map(User::hideAuthData).toList());
  }

  @CrossOrigin
  @PostMapping(value = "/login") // , consumes = {"application/json;charset=UTF-8"})
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    Optional<String> optionalTokenString =
        this.userService.logIn(loginRequest.getUsername(), loginRequest.getPassword());

    if (optionalTokenString.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalTokenString.get());
  }

  @CrossOrigin
  @PostMapping(value = "/create") // , consumes = {"application/json;charset=UTF-8"})
  public ResponseEntity<User> createUser(@RequestBody User user
      //          , @RequestHeader("token") String token
      ) {
    //    if (!verifierRequest.isAdmin(token)) {
    //      return ResponseEntity.ok().build();
    //    }

    Optional<User> optionalUser = this.userService.create(user);

    if (optionalUser.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalUser.get().hideAuthData());
  }

  @CrossOrigin
  @PostMapping(value = "/update") // , consumes = {"application/json;charset=UTF-8"})
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

  @CrossOrigin
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
    private String username;
    private String password;
  }
}
