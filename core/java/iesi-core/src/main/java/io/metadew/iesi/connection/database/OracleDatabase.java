package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.oracle.OracleDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class OracleDatabase extends SchemaDatabase {

    public OracleDatabase(OracleDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    public OracleDatabase(OracleDatabaseConnection databaseConnection, int initialPoolSize, int maximalPoolSize, String schema) {
        super(databaseConnection, initialPoolSize, maximalPoolSize, schema);
    }

}
