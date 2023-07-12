package assignsShifts.configuration;

import assignsShifts.exceptions.ValidationParamException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class DBConnectionConfiguration {
    @Value("${assigns.shifts.database.address:}")
    private String databaseAddress;

    @Value("${assigns.shifts.database.port:}")
    private String databasePort;

    @Value("${assigns.shifts.database.database:}")
    private String databaseDatabase;

    private void validateDataBaseParams() throws ValidationParamException {
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
        
        return String.format("mongodb://%s:%s/%s", this.databaseAddress, this.databasePort, this.databaseDatabase);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws ValidationParamException {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(getConnectionString()));
    }
}
