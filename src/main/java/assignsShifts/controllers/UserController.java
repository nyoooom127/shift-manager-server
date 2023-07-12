package assignsShifts.controllers;

import assignsShifts.JWT.VerifierRequest;
import assignsShifts.models.User;
import assignsShifts.models.enums.UserPermissionsEnum;
import assignsShifts.services.UserService;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
  @Autowired private UserService userService;
  @Autowired private VerifierRequest verifierRequest;

  @GetMapping(value = "/user")
  public ResponseEntity<List<User>> update(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.userService.findAll());
  }

  @PostMapping(value = "/user/login")
  public ResponseEntity<Token> login(@RequestBody LoginRequest loginRequest) {
    Optional<String> optionalTokenString =
        this.userService.logIn(loginRequest.getUserName(), loginRequest.getPassword());

    if (optionalTokenString.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(Token.builder().tokenString(optionalTokenString.get()).build());
  }

  @PostMapping(value = "/user/create")
  public ResponseEntity<Optional<User>> create(
      @RequestBody User user, @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.userService.create(user));
  }

  @PostMapping(value = "/user/eden")
  public ResponseEntity<List<User>> create(@RequestBody List<User> users) {
    List<User> result = new ArrayList<>();

    users.forEach(user -> this.userService.create(user).ifPresent(result::add));

    return ResponseEntity.ok(result);
  }

  @PostMapping(value = "/user/update")
  public ResponseEntity<Optional<User>> update(
      @RequestBody User user, @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.userService.update(user));
  }

  @PostMapping(value = "/user/constraints/add")
  public ResponseEntity<Optional<User>> addConstraints(
      @RequestBody AddConstraintsRequest addConstraintsRequest,
      @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.USER)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.ok(
        this.userService.addConstraints(
            addConstraintsRequest.getUserId(), addConstraintsRequest.getDateInMillis()));
  }

  @PostMapping(value = "/user/constraints/add/eden")
  public ResponseEntity<List<User>> addConstraints(
      @RequestBody List<AddConstraintsRequest> addConstraintsRequestList,
      @RequestHeader("token") String token) {
    List<User> result = new ArrayList<>();

    addConstraintsRequestList.forEach(
        addConstraintsRequest ->
            this.addConstraints(addConstraintsRequest, token).getBody().ifPresent(result::add));

    return ResponseEntity.ok(result);
  }

  @PostMapping(value = "/user/constraints/remove")
  public ResponseEntity<Optional<User>> removeConstraints(
      @RequestBody RemoveConstraintsRequest removeConstraintsRequest,
      @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.USER)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(
        this.userService.removeConstraints(
            removeConstraintsRequest.getUserId(),
            removeConstraintsRequest.getConstraintsUUIDString()));
  }

  @Getter
  public static class LoginRequest {
    private String userName;
    private String password;
  }

  @Getter
  private static class AddConstraintsRequest {
    private String userId;
    private long dateInMillis;
  }

  @Getter
  private static class RemoveConstraintsRequest {
    private String userId;
    private String constraintsUUIDString;
  }

  @Data
  @Builder
  private static class Token {
    private String tokenString;
  }
}
