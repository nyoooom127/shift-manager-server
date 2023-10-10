package assignsShifts.entities.shift.type;

import assignsShifts.JWT.VerifierRequest;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shiftType")
public class ShiftTypeController {
  @Autowired private ShiftTypeService shiftTypeService;
  @Autowired private VerifierRequest verifierRequest;

  @GetMapping
  public ResponseEntity<List<ShiftType>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.shiftTypeService.findAll());
  }

  @PostMapping(value = "/create")
  public ResponseEntity<ShiftType> createShiftType(
      @RequestBody ShiftType shiftType, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<ShiftType> optionalShiftType = this.shiftTypeService.create(shiftType);

    if (optionalShiftType.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalShiftType.get());
  }

  @PostMapping(value = "/update")
  public ResponseEntity<ShiftType> updateShiftType(
      @RequestBody ShiftType shiftType, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<ShiftType> optionalShiftType = this.shiftTypeService.update(shiftType);

    if (optionalShiftType.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalShiftType.get());
  }

  @DeleteMapping
  public ResponseEntity<DeleteResult> deleteShiftType(
      @RequestParam String id, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<DeleteResult> optionalDeleteResult = shiftTypeService.delete(id);

    if (optionalDeleteResult.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalDeleteResult.get());
  }
}
