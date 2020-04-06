package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.postgresql.PostgresqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
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

