package io.metadew.iesi.connection.database.h2;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class H2MemoryDatabaseConnection extends H2DatabaseConnection {

    public H2MemoryDatabaseConnection(String databaseName, String userName, String userPassword, String initSql) {
        super("jdbc:h2:mem:" + databaseName + ";ALIAS_COLUMN_NAME=true", userName, userPassword, initSql);
    }

    public H2MemoryDatabaseConnection(String databaseName, String userName, String userPassword, String initSql, String schema) {
        super("jdbc:h2:mem:" + databaseName + ";ALIAS_COLUMN_NAME=true", userName, userPassword, initSql, schema);
    }
}
