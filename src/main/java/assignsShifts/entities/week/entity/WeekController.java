package assignsShifts.entities.week.entity;

import assignsShifts.JWT.VerifierRequest;
import assignsShifts.models.enums.UserPermissionsEnum;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/week")
public class WeekController {
  @Autowired private WeekService weekService;
  @Autowired private VerifierRequest verifierRequest;

  @GetMapping
  public ResponseEntity<List<Week>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.weekService.findAll());
  }

  @PostMapping(value = "/calculate")
  public ResponseEntity<Week> calculateWeek(
      @RequestBody Week week, @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.weekService.calculateWeek(week));
  }

  @PostMapping(value = "/create")
  public ResponseEntity<Week> createWeek(
      @RequestBody Week week, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<Week> optionalWeek = this.weekService.create(week);

    if (optionalWeek.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalWeek.get());
  }

  @PostMapping(value = "/update")
  public ResponseEntity<Week> updateWeek(
      @RequestBody Week week, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<Week> optionalWeek = this.weekService.update(week);

    if (optionalWeek.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalWeek.get());
  }

  @DeleteMapping
  public ResponseEntity<DeleteResult> deleteWeek(
      @RequestParam String id, @RequestHeader("token") String token) {
    if (!verifierRequest.isAdmin(token)) {
      return ResponseEntity.ok().build();
    }

    Optional<DeleteResult> optionalDeleteResult = weekService.delete(id);

    if (optionalDeleteResult.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(optionalDeleteResult.get());
  }
}
