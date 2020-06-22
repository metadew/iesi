package io.metadew.iesi.connection.database.h2;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class H2ServerDatabaseConnection extends H2DatabaseConnection {

    public H2ServerDatabaseConnection(String hostName, int portNumber, String filePath, String userName, String userPassword, String initSql) {
        super("jdbc:h2:tcp://" + hostName + ":" + portNumber + "/" + filePath, userName, userPassword, initSql);
    }

    public H2ServerDatabaseConnection(String hostName, int portNumber, String filePath, String userName, String userPassword, String initSql, String schema) {
        super("jdbc:h2:tcp://" + hostName + ":" + portNumber + "/" + filePath, userName, userPassword, initSql, schema);
    }

}
