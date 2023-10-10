package assignsShifts.entities.week.type;

import assignsShifts.JWT.VerifierRequest;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/weekType")
public class WeekTypeController {
  @Autowired private WeekTypeService weekTypeService;
  @Autowired private VerifierRequest verifierRequest;

  @GetMapping
  public ResponseEntity<List<WeekType>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.weekTypeService.findAll());
  }

  @PostMapping(value = "/create")
  public ResponseEntity<WeekType> createWeekType(
      @RequestBody WeekType weekType, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<WeekType> optionalWeekType = this.weekTypeService.create(weekType);

    if (optionalWeekType.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalWeekType.get());
  }

  @PostMapping(value = "/update")
  public ResponseEntity<WeekType> updateWeekType(
      @RequestBody WeekType weekType, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<WeekType> optionalWeekType = this.weekTypeService.update(weekType);

    if (optionalWeekType.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalWeekType.get());
  }

  @DeleteMapping
  public ResponseEntity<DeleteResult> deleteWeekType(
      @RequestParam String id, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<DeleteResult> optionalDeleteResult = weekTypeService.delete(id);

    if (optionalDeleteResult.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalDeleteResult.get());
  }
}
