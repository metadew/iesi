package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.postgresql.PostgresqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class PostgresqlDatabase extends SchemaDatabase {

    public PostgresqlDatabase(PostgresqlDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }
    
}

