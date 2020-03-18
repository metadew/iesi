package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.mssql.MssqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class MssqlDatabase extends SchemaDatabase {

    public MssqlDatabase(MssqlDatabaseConnection databaseConnection, String schema)  {
        super(databaseConnection, schema);
    }

}
