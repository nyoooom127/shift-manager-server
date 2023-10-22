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

  @CrossOrigin
  @GetMapping
  public ResponseEntity<List<Week>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.weekService.findAll());
  }

  @CrossOrigin
  @PostMapping(value = "/calculate")//, consumes = {"application/json;charset=UTF-8"})
  public ResponseEntity<Week> calculateWeek(
      @RequestBody Week week, @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.weekService.calculateWeek(week));
  }

  @CrossOrigin
  @PostMapping(value = "/create")//, consumes = {"application/json;charset=UTF-8"})
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

  @CrossOrigin
  @PostMapping(value = "/update")//, consumes = {"application/json;charset=UTF-8"})
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

  @CrossOrigin
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
