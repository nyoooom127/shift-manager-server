package assignsShifts.entities.user.entity;

import assignsShifts.JWT.VerifierRequest;
import assignsShifts.entities.constraint.entity.Constraint;
import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.entities.user.type.UserType;
import assignsShifts.entities.week.type.Test2Repository;
import assignsShifts.entities.week.type.TestRepository;
import assignsShifts.models.Model;
import assignsShifts.models.enums.UserPermissionsEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mongodb.client.result.DeleteResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
  @PostMapping(value = "/login")//, consumes = {"application/json;charset=UTF-8"})
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    Optional<String> optionalTokenString =
        this.userService.logIn(loginRequest.getUsername(), loginRequest.getPassword());

    if (optionalTokenString.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalTokenString.get());
  }

  @CrossOrigin
  @PostMapping(value = "/create")//, consumes = {"application/json;charset=UTF-8"})
  public ResponseEntity<User> createUser(
      @RequestBody User user
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
  @PostMapping(value = "/update")//, consumes = {"application/json;charset=UTF-8"})
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

  @Autowired
  TestRepository testRepository;

  @Autowired
  Test2Repository testRepository2;

  @GetMapping("/test")
  public ResponseEntity<List<User1>> get(){
    return ResponseEntity.ok(testRepository.findAll());
  }

  @PostMapping("/test")
  public ResponseEntity<User1> test(@RequestBody User1 user1){
    Optional<User1> user11 = testRepository.save(user1);

    if(user11.isEmpty()){
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.ok(user11.get());
  }

  @PostMapping("/test2")
  public ResponseEntity<User2> test2(@RequestBody User2 user1){
    Optional<User2> user11 = testRepository2.save(user1);

    if(user11.isEmpty()){
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.ok(user11.get());
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Document("users1")
  public static class User1 extends Model {
      @NonNull
    private String fullName;
//      @JsonManagedReference
//      @JsonBackReference
    @DBRef(lazy = true)
//@DocumentReference
    private List<User2> types;
//    @DBRef private Map<ShiftType, Integer> numShifts;
    private assignsShifts.entities.user.entity.User.AuthorizationData authorizationData;

//    @DBRef
    //  @NonNull
//    private List<Constraint> constraints;

//    @DBRef
    //  @NonNull
//    private List<Shift> shifts;

    private boolean active;
    //  @DBRef private Shift mostRecentShift;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Document("users2")
  public static class User2 extends Model {
    @NonNull
    private String fullName;

    @DBRef(lazy = true)
//    @JsonBackReference
//    @DocumentReference
    private List<User1> types;
//    @DBRef private List<UserType> types;
    //    @DBRef private Map<ShiftType, Integer> numShifts;
    private assignsShifts.entities.user.entity.User.AuthorizationData authorizationData;

//    @DBRef
    //  @NonNull
//    private List<Constraint> constraints;

//    @DBRef
    //  @NonNull
//    private List<Shift> shifts;

    private boolean active;
    //  @DBRef private Shift mostRecentShift;
  }

    @Getter
  public static class LoginRequest {
    private String username;
    private String password;
  }
}
