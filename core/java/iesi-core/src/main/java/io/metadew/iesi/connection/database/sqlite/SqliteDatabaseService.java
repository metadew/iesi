package io.metadew.iesi.connection.database.sqlite;

import io.metadew.iesi.connection.database.DatabaseService;
import io.metadew.iesi.connection.database.IDatabaseService;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandler;
import io.metadew.iesi.metadata.definition.MetadataField;

import java.sql.Connection;
import java.sql.SQLException;

public class SqliteDatabaseService extends DatabaseService<SqliteDatabase> implements IDatabaseService<SqliteDatabase> {

    private static SqliteDatabaseService INSTANCE;

    private static final String keyword= "db.sqlite";


    public synchronized static SqliteDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SqliteDatabaseService();
        }
        return INSTANCE;
    }

    private SqliteDatabaseService() {}

    @Override
    public SqliteDatabase getDatabase(io.metadew.iesi.metadata.definition.connection.Connection connection) {
        return null;
    }

    @Override
    public String keyword() {
        return keyword;
    }

    public Connection getConnection(SqliteDatabase sqliteDatabase) {
        synchronized (sqliteDatabase.getConnectionPoolLock()) {
            return DatabaseConnectionHandler.getInstance().getConnection(sqliteDatabase.getDatabaseConnection());
        }
    }


    public boolean isInitializeConnectionPool() {
        return false;
    }


    public void releaseConnection(SqliteDatabase sqliteDatabase, Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public String getSystemTimestampExpression(SqliteDatabase sqliteDatabase) {
        return "datetime(CURRENT_TIMESTAMP, 'localtime')";
    }

    @Override
    public String getAllTablesQuery(SqliteDatabase sqliteDatabase, String pattern) {
        return "select tbl_name 'TABLE_NAME', '' 'OWNER' from sqlite_master where tbl_name like '"
                + pattern
                + "%' order by tbl_name asc";
    }

    @Override
    public String createQueryExtras(SqliteDatabase sqliteDatabase) {
        return "";
    }

    @Override
    public boolean addComments(SqliteDatabase sqliteDatabase) {
        return false;
    }

    @Override
    public String toQueryString(SqliteDatabase sqliteDatabase, MetadataField field) {
        StringBuilder fieldQuery = new StringBuilder();
        // Data Types
        switch (field.getType()) {
            case "string":
                fieldQuery.append("TEXT");
                break;
            case "flag":
                fieldQuery.append("TEXT");
                break;
            case "number":
                fieldQuery.append("NUMERIC");
                break;
            case "timestamp":
                fieldQuery.append("TEXT");
                break;
        }

        // Default DtTimestamp
        if (field.isDefaultTimestamp()) {
            fieldQuery.append(" DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))");
        }

        // Nullable
        if (!field.isNullable()) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<SqliteDatabase> appliesTo() {
        return SqliteDatabase.class;
    }


}
