package assignsShifts.entities.user.type;

import assignsShifts.JWT.VerifierRequest;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/userType")
public class UserTypeController {
  @Autowired private UserTypeService userTypeService;
  @Autowired private VerifierRequest verifierRequest;

  @GetMapping
  public ResponseEntity<List<UserType>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.userTypeService.findAll());
  }

  @PostMapping(value = "/create")
  public ResponseEntity<UserType> createUserType(
      @RequestBody UserType userType, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<UserType> optionalUserType = this.userTypeService.create(userType);

    if (optionalUserType.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalUserType.get());
  }

  @PostMapping(value = "/update")
  public ResponseEntity<UserType> updateUserType(
      @RequestBody UserType userType, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<UserType> optionalUserType = this.userTypeService.update(userType);

    if (optionalUserType.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalUserType.get());
  }

  @DeleteMapping
  public ResponseEntity<DeleteResult> deleteUserType(
      @RequestParam String id, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<DeleteResult> optionalDeleteResult = userTypeService.delete(id);

    if (optionalDeleteResult.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalDeleteResult.get());
  }
}
