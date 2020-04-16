package io.metadew.iesi.connection.database.sqlite;

import io.metadew.iesi.connection.database.Database;

public class SqliteDatabase extends Database {

    private Object connectionPoolLock = new Object();

    public SqliteDatabase(SqliteDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

    public Object getConnectionPoolLock() {
        return connectionPoolLock;
    }

}
