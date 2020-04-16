package io.metadew.iesi.connection.database.connection.h2;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class H2MemoryDatabaseConnection extends H2DatabaseConnection {

    public H2MemoryDatabaseConnection(String databaseName, String userName, String userPassword) {
        super("jdbc:h2:mem:" + databaseName, userName, userPassword);
    }

    public H2MemoryDatabaseConnection(String databaseName, String userName, String userPassword, String schema) {
        super("jdbc:h2:mem:" + databaseName, userName, userPassword, schema);
    }
}
