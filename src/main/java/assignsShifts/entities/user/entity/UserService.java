package assignsShifts.entities.user.entity;

import assignsShifts.abstractClasses.AbstractService;
import assignsShifts.entities.constraint.entity.Constraint;
import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.entities.user.type.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractService<User> {
  @Autowired private UserRepository userRepository;

  //  @Autowired private MongoUserRepository mongoUserRepository;

  public List<User> findAllWithoutLists() {
    //      return mongoUserRepository.findAllIncludeAllButShiftsAndConstraintsFields();
    return userRepository.findAllNoLists();
  }

  public List<User> findByType(List<UserType> userTypes) {
    return this.userRepository.findByType(userTypes);
  }

  public Optional<String> logIn(String userName, String password) {
    return this.userRepository.logIn(userName, password);
  }

  @Override
  public Optional<User> create(User entity) {
    List<User> allUsers = this.findByType(entity.getTypes());
    entity.setInitialScores(new HashMap<>());
    List<ShiftType> shiftTypes =
        entity.getTypes().stream()
            .flatMap(
                userType -> userType.getAllowedShiftTypes().stream()
                //                                         .map(Model::getId)
                )
            .distinct()
            .toList();

    shiftTypes.forEach(
        shiftType -> {
          List<Double> scores =
              allUsers.stream()
                  .filter(
                      user ->
                          user.getTypes().stream()
                              .anyMatch(
                                  userType -> userType.getAllowedShiftTypes().contains(shiftType)))
                  .map(user -> user.getShiftScore(shiftType, true))
                  .sorted()
                  .toList();
          double median;

          if (scores.size() % 2 == 0) {
            median = (scores.get(scores.size() / 2) + scores.get(scores.size() / 2 - 1)) / 2;
          } else {
            median = scores.get(scores.size() / 2);
          }

          entity.getInitialScores().put(shiftType.getId(), median);
        });

    return super.create(entity);
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
