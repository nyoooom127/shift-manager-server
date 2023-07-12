package assignsShifts.repository;

import assignsShifts.JWT.JWTTokenCreator;
import assignsShifts.models.User;
import assignsShifts.models.enums.UserPermissionsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
  @Autowired private MongoTemplate mongoTemplate;
  @Autowired private JWTTokenCreator jwtTokenCreator;

  public Optional<String> logIn(String userName, String password) {
    Query query =
        Query.query(
            Criteria.where("authorizationData.userName")
                .is(userName)
                .and("authorizationData.password")
                .is(password));
    Optional<User> optionalUser =
        Optional.ofNullable(this.mongoTemplate.findOne(query, User.class));

    if (optionalUser.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(this.jwtTokenCreator.createJWTToken(optionalUser.get().getId()));
  }

  public Optional<User> create(User user) {
    this.mongoTemplate.save(user);

    return Optional.of(this.mongoTemplate.save(user));
  }

  public Optional<User> update(User user) {
    return Optional.of(this.mongoTemplate.save(user));
  }

  public List<User> findAll() {
    return this.mongoTemplate.findAll(User.class);
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

  public Optional<User> findById(String userId) {
    return Optional.ofNullable(this.mongoTemplate.findById(userId, User.class));
  }

  public Optional<User> addConstraints(String userId, User.Constraint constraint) {
    Optional<User> optionalUser = this.findById(userId);

    if (optionalUser.isEmpty()) {
      return Optional.empty();
    }

    User user = optionalUser.get();
    user.getConstraints().add(constraint);

    return this.update(user);
  }

  public Optional<User> removeConstraints(String userId, String constraintsUUIDString) {
    Optional<User> optionalUser = this.findById(userId);

    if (optionalUser.isEmpty()) {
      return Optional.empty();
    }

    User user = optionalUser.get();
    user.getConstraints()
        .removeIf(constraint -> constraint.getUuidString().equals(constraintsUUIDString));

    return this.update(user);
  }
}
