package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

import java.sql.Connection;
import java.sql.SQLException;

public class SqliteDatabase extends Database {

    public SqliteDatabase(SqliteDatabaseConnection databaseConnection) {
        super(databaseConnection, 0, 0);
    }


}
