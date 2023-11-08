package assignsShifts.entities.user.entity;

import assignsShifts.JWT.JWTTokenCreator;
import assignsShifts.abstractClasses.AbstractRepository;
import assignsShifts.entities.constraint.entity.Constraint;
import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.user.type.UserType;
import assignsShifts.models.enums.UserPermissionsEnum;
import assignsShifts.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends AbstractRepository<User> {

  @Autowired private JWTTokenCreator jwtTokenCreator;

  public List<User> findByType(List<UserType> userTypes) {
    Query query = Query.query(Criteria.where("types").in(userTypes));

    return this.mongoTemplate.find(query, getType());
  }

  public Optional<String> logIn(String userName, String password) {
    Query query =
        Query.query(
            Criteria.where("authorizationData.username")
                .is(userName)
                .and("authorizationData.password")
                .is(password));
    Optional<User> optionalUser =
        Optional.ofNullable(this.mongoTemplate.findOne(query, User.class));

    if (optionalUser.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(this.jwtTokenCreator.createJWTToken(optionalUser.get()));
  }

  public Optional<User> addConstraint(String userId, Constraint constraint) {
    Optional<User> optionalUser = this.findById(userId);

    if (optionalUser.isEmpty()) {
      return Optional.empty();
    }

    User user = optionalUser.get();
    user.getConstraints().add(constraint);

    return this.save(user);
  }

  public Optional<User> removeConstraint(String userId, String constraintId) {
    Optional<User> optionalUser = this.findById(userId);

    if (optionalUser.isEmpty()) {
      return Optional.empty();
    }

    User user = optionalUser.get();
    user.getConstraints().removeIf(constraint -> constraint.getId().equals(constraintId));

    return this.save(user);
  }

  public Optional<User> addShift(String userId, Shift shift) {
    Optional<User> optionalUser = this.findById(userId);

    if (optionalUser.isEmpty()) {
      return Optional.empty();
    }

    User user = optionalUser.get();
    user.addShift(shift);
    //    user.getShifts().add(shift);
    //    user.getNumShifts()
    //        .put(
    //            shift.getType().getId(),
    //            user.getNumShifts().getOrDefault(shift.getType().getId(), 0) + 1);

    return this.save(user);
  }

  public Optional<User> removeShift(String userId, String shiftId) {
    Optional<User> optionalUser = this.findById(userId);

    if (optionalUser.isEmpty()) {
      return Optional.empty();
    }

    User user = optionalUser.get();
    Optional<Shift> optionalShift =
        user.getShifts().stream().filter(shift -> shift.getId().equals(shiftId)).findFirst();

    if (optionalShift.isPresent()) {
      user.removeShift(optionalShift.get());
      //      user.getShifts().removeIf(shift -> shift.getId().equals(shiftId));
      //
      //      if (DateUtil.isWeekend(optionalShift.get().getStartDate())) {
      //        user.getNumWeekendShifts()
      //            .put(
      //                optionalShift.get().getType().getId(),
      //
      // user.getNumWeekendShifts().getOrDefault(optionalShift.get().getType().getId(), 1)
      //                    - 1);
      //      } else {
      //        user.getNumShifts()
      //            .put(
      //                optionalShift.get().getType().getId(),
      //                user.getNumShifts().getOrDefault(optionalShift.get().getType().getId(), 1) -
      // 1);
      //      }
    }

    return this.save(user);
  }

  public boolean isVerify(String userId, UserPermissionsEnum userPermissionsEnum) {
    User user = this.mongoTemplate.findById(userId, User.class);

    if (user == null) {
      return false;
    }

    if (user.getAuthorizationData().getUserPermissions().equals(UserPermissionsEnum.ADMIN)) {
      return true;
    }

    return user.getAuthorizationData().getUserPermissions().equals(userPermissionsEnum);
  }

  @Override
  protected Class<User> getType() {
    return User.class;
  }
}
