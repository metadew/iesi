package io.metadew.iesi.connection.database.mysql;

import io.metadew.iesi.connection.database.SchemaDatabase;

public class MysqlDatabase extends SchemaDatabase {

    public MysqlDatabase(MysqlDatabaseConnection mysqlDatabaseConnection, String schema) {
        super(mysqlDatabaseConnection, schema);
    }

    public MysqlDatabase(MysqlDatabaseConnection mysqlDatabaseConnection) {
        super(mysqlDatabaseConnection);
    }

    public MysqlDatabase(MysqlDatabaseConnection mysqlDatabaseConnection, int initialPoolSize, int maximalPoolSize, String schema) {
        super(mysqlDatabaseConnection, initialPoolSize, maximalPoolSize, schema);
    }

    public MysqlDatabase(MysqlDatabaseConnection mysqlDatabaseConnection, int initialPoolSize, int maximalPoolSize) {
        super(mysqlDatabaseConnection, initialPoolSize, maximalPoolSize);
    }
}
