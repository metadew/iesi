package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;

public class SqliteDatabase extends Database {

    private Object connectionPoolLock;

    public SqliteDatabase(SqliteDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public Object getConnectionPoolLock() {
        return connectionPoolLock;
    }

}
