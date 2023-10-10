package assignsShifts.entities.constraint.entity;

import assignsShifts.JWT.VerifierRequest;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/constraint")
public class ConstraintController {
  @Autowired private ConstraintService constraintService;
  @Autowired private VerifierRequest verifierRequest;

  @GetMapping
  public ResponseEntity<List<Constraint>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.constraintService.findAll());
  }

  @PostMapping(value = "/create")
  public ResponseEntity<Constraint> createConstraint(
          @RequestBody Constraint constraint, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<Constraint> optionalConstraint = this.constraintService.create(constraint);

    if (optionalConstraint.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalConstraint.get());
  }

  @PostMapping(value = "/update")
  public ResponseEntity<Constraint> updateConstraint(
          @RequestBody Constraint constraint, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<Constraint> optionalConstraint = this.constraintService.update(constraint);

    if (optionalConstraint.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalConstraint.get());
  }

  @DeleteMapping
  public ResponseEntity<DeleteResult> deleteConstraint(
      @RequestParam String id, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<DeleteResult> optionalDeleteResult = constraintService.delete(id);

    if (optionalDeleteResult.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalDeleteResult.get());
  }
}
