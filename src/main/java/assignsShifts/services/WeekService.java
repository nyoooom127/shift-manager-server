package assignsShifts.services;

import assignsShifts.logic.ScoreCalculator;
import assignsShifts.models.User;
import assignsShifts.models.Week;
import assignsShifts.repository.UserRepository;
import assignsShifts.repository.WeekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WeekService {
  @Autowired private WeekRepository weekRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ScoreCalculator scoreCalculator;

  public List<Week> findAll() {
    return this.weekRepository.findAll();
  }

  public Optional<Week> findActive() {
    return this.weekRepository.findActive();
  }

  public Optional<Week> createWeek(Week week) {
    week.setUUIDString(UUID.randomUUID().toString());

    return this.weekRepository.save(week);
  }

  public Optional<Week> updateWeek(Week week) {
    if (this.weekRepository.findById(week.getUUIDString()).isEmpty()) {
      return Optional.empty();
    }

    return this.weekRepository.save(week);
  }

  public Optional<Week> closeWeek(String uuidString) {
    Optional<Week> optionalWeek = this.weekRepository.findById(uuidString);

    if (optionalWeek.isEmpty()) {
      return Optional.empty();
    }

    Week week = optionalWeek.get();

    week.setClosed(true);
    List<User> userList = this.scoreCalculator.calculationShiftWeek(week);

    userList.forEach(user -> this.userRepository.update(user));

    return this.weekRepository.save(week);
  }
}
