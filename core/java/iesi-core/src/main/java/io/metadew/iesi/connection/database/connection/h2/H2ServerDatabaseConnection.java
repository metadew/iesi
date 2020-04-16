package io.metadew.iesi.connection.database.connection.h2;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class H2ServerDatabaseConnection extends H2DatabaseConnection {

    public H2ServerDatabaseConnection(String hostName, int portNumber, String filePath, String userName, String userPassword) {
        super("jdbc:h2:tcp://" + hostName + ":" + portNumber + "/" + filePath, userName, userPassword);
    }

    public H2ServerDatabaseConnection(String hostName, int portNumber, String filePath, String userName, String userPassword, String schema) {
        super("jdbc:h2:tcp://" + hostName + ":" + portNumber + "/" + filePath, userName, userPassword, schema);
    }

}
