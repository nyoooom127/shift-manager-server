package assignsShifts.services;

import assignsShifts.models.User;
import assignsShifts.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
  @Autowired private UserRepository userRepository;

  public Optional<String> logIn(String userName, String password) {
    return this.userRepository.logIn(userName, password);
  }

  public Optional<User> create(User user) {
    user.setId(UUID.randomUUID().toString());

    return this.userRepository.create(user);
  }

  public Optional<User> update(User user) {
    if (this.userRepository.findById(user.getId()).isEmpty()) {
      return Optional.empty();
    }

    return this.userRepository.update(user);
  }

  public List<User> findAll() {
    return this.userRepository.findAll();
  }

  public Optional<User> addConstraints(String userId, long dateInMillis) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(dateInMillis);

    User.Constraint constraint = new User.Constraint(UUID.randomUUID().toString(), calendar);

    return this.userRepository.addConstraints(userId, constraint);
  }

  public Optional<User> removeConstraints(String userId, String constraintsUUIDString) {
    return this.userRepository.removeConstraints(userId, constraintsUUIDString);
  }
}
