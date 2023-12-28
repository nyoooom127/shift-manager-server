package assignsShifts.configuration;

import assignsShifts.exceptions.ValidationParamException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class DBConnectionConfiguration {
  @Value("${assigns.shifts.database.username:}")
  private String databaseUsername;

  @Value("${assigns.shifts.database.password:}")
  private String databasePassword;

  @Value("${assigns.shifts.database.address:}")
  private String databaseAddress;

  @Value("${assigns.shifts.database.port:}")
  private String databasePort;

  @Value("${assigns.shifts.database.database:}")
  private String databaseDatabase;

  private void validateDataBaseParams() throws ValidationParamException {
    if (Strings.isBlank(databaseUsername)) {
      throw new ValidationParamException("assigns.shifts.database.username");
    }

        if (Strings.isBlank(databasePassword)) {
      throw new ValidationParamException("assigns.shifts.database.password");
    }

        if (Strings.isBlank(databaseAddress)) {
      throw new ValidationParamException("assigns.shifts.database.address");
    }

    if (Strings.isBlank(databasePort)) {
      throw new ValidationParamException("assigns.shifts.database.port");
    }

    if (Strings.isBlank(databaseDatabase)) {
      throw new ValidationParamException("assigns.shifts.database.database");
    }
  }

  private String getConnectionString() throws ValidationParamException {
    this.validateDataBaseParams();

//    "mongodb+srv://shiftManager:PwILYW3ryFgcIw6h@shiftmanager.dvvn49n.mongodb.net/shiftManager"

    return String.format(
            "mongodb+srv://%s:%s@%s/%s",this.databaseUsername, this.databasePassword, this.databaseAddress, this.databaseDatabase);
  }

  @Bean
  public MongoTemplate mongoTemplate() throws ValidationParamException {
    return new MongoTemplate(new SimpleMongoClientDatabaseFactory(getConnectionString()));
  }

//  @Bean
//  MongoMappingContext springDataMongoMappingContext() {
//    return new MongoMappingContext();
//  }
}
