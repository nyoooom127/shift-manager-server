package assignsShifts.entities.constraint.type;

import assignsShifts.JWT.VerifierRequest;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/constraintType")
public class ConstraintTypeController {
  @Autowired private ConstraintTypeService constraintTypeService;
  @Autowired private VerifierRequest verifierRequest;

  @CrossOrigin
  @GetMapping
  public ResponseEntity<List<ConstraintType>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.constraintTypeService.findAll());
  }

  @CrossOrigin
  @PostMapping(value = "/create")//, consumes = {"application/json;charset=UTF-8"})
  public ResponseEntity<ConstraintType> createConstraintType(
      @RequestBody ConstraintType constraintType, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<ConstraintType> optionalConstraintType =
        this.constraintTypeService.create(constraintType);

    if (optionalConstraintType.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalConstraintType.get());
  }

  @CrossOrigin
  @PostMapping(value = "/update")//, consumes = {"application/json;charset=UTF-8"})
  public ResponseEntity<ConstraintType> updateConstraintType(
      @RequestBody ConstraintType constraintType, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<ConstraintType> optionalConstraintType =
        this.constraintTypeService.update(constraintType);

    if (optionalConstraintType.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalConstraintType.get());
  }

  @CrossOrigin
  @DeleteMapping
  public ResponseEntity<DeleteResult> deleteConstraintType(
      @RequestParam String id, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<DeleteResult> optionalDeleteResult = constraintTypeService.delete(id);

    if (optionalDeleteResult.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalDeleteResult.get());
  }
}
