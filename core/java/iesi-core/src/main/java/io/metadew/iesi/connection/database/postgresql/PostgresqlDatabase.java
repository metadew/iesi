package io.metadew.iesi.connection.database.postgresql;

import io.metadew.iesi.connection.database.SchemaDatabase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PostgresqlDatabase extends SchemaDatabase {

    public PostgresqlDatabase(PostgresqlDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    public PostgresqlDatabase(PostgresqlDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }
    
}

