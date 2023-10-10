package assignsShifts.entities.user.entity;

import assignsShifts.abstractClasses.AbstractService;
import assignsShifts.entities.constraint.entity.Constraint;
import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.user.type.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractService<User> {
  @Autowired private UserRepository userRepository;

  public List<User> findByType(List<UserType> userTypes) {
    return this.userRepository.findByType(userTypes);
  }

  public Optional<String> logIn(String userName, String password) {
    return this.userRepository.logIn(userName, password);
  }

  public Optional<User> addConstraint(String userId, Constraint constraint) {
    return this.userRepository.addConstraint(userId, constraint);
  }

  public Optional<User> removeConstraint(String userId, String constraintId) {
    return this.userRepository.removeConstraint(userId, constraintId);
  }

  public Optional<User> addShift(String userId, Shift shift) {
    return this.userRepository.addShift(userId, shift);
  }

  public Optional<User> removeShift(String userId, String shiftId) {
    return this.userRepository.removeShift(userId, shiftId);
  }
}
