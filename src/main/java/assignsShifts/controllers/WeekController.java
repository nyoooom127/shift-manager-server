package assignsShifts.controllers;

import assignsShifts.JWT.VerifierRequest;
import assignsShifts.logic.ShiftCalculator;
import assignsShifts.logic.WeekLogic;
import assignsShifts.models.Week;
import assignsShifts.models.enums.UserPermissionsEnum;
import assignsShifts.services.UserService;
import assignsShifts.services.WeekService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class WeekController {
  @Autowired private ShiftCalculator shiftCalculator;
  @Autowired private UserService userService;
  @Autowired private WeekService weekService;
  @Autowired private VerifierRequest verifierRequest;
  @Autowired private WeekLogic weekLogic;

  @PostMapping(value = "/week/calculate")
  public ResponseEntity<Week> calculationShiftWeek(
      @RequestBody Week week, @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(
        this.weekLogic.removeUserDetails(
            this.shiftCalculator.calculationShiftWeek(week, userService.findAll())));
  }

  @GetMapping(value = "/week")
  public ResponseEntity<List<Week>> findAll(@RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    List<Week> result = new ArrayList<>();
    this.weekService.findAll().forEach(week -> result.add(this.weekLogic.removeUserDetails(week)));

    return ResponseEntity.ok(result);
  }

  @CrossOrigin
  @GetMapping(value = "/week/active")
  public ResponseEntity<Week> findActive() {
    Optional<Week> optionalWeek = this.weekService.findActive();

    if (optionalWeek.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.weekLogic.removeUserDetails(optionalWeek.get()));
  }

  @PostMapping(value = "/week/create")
  public ResponseEntity<Week> createWeek(
      @RequestBody Week week, @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    Optional<Week> optionalWeek = this.weekService.createWeek(week);

    if (optionalWeek.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.weekLogic.removeUserDetails(optionalWeek.get()));
  }

  @PostMapping(value = "/week/update")
  public ResponseEntity<Week> updateWeek(
      @RequestBody Week week, @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    Optional<Week> optionalWeek = this.weekService.updateWeek(week);

    if (optionalWeek.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.weekLogic.removeUserDetails(optionalWeek.get()));
  }

  @PostMapping(value = "/week/close")
  public ResponseEntity<Week> closeWeek(
      @RequestBody CloseWeekRequest uuidString, @RequestHeader("token") String token) {
    if (!verifierRequest.isVerify(token, UserPermissionsEnum.ADMIN)) {
      return ResponseEntity.ok().build();
    }

    Optional<Week> optionalWeek = this.weekService.closeWeek(uuidString.getUuidString());

    if (optionalWeek.isEmpty()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.ok(this.weekLogic.removeUserDetails(optionalWeek.get()));
  }

  @Getter
  private static class CloseWeekRequest {
    private String uuidString;
  }
}
