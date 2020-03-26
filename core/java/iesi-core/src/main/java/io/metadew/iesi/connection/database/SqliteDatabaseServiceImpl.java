package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.metadata.definition.MetadataField;

import java.sql.Connection;
import java.sql.SQLException;

public class SqliteDatabaseServiceImpl extends DatabaseServiceImpl<SqliteDatabase> implements DatabaseService<SqliteDatabase>  {

    private static SqliteDatabaseServiceImpl INSTANCE;

    public synchronized static SqliteDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SqliteDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private SqliteDatabaseServiceImpl() {}

    @Override
    public SqliteDatabase getDatabase(io.metadew.iesi.metadata.definition.connection.Connection connection) {
        return null;
    }

    @Override
    public String keyword() {
        return null;
    }

    public Connection getConnection(SqliteDatabase sqliteDatabase) {
        synchronized (sqliteDatabase.getConnectionPoolLock()) {
            return DatabaseConnectionHandlerImpl.getInstance().getConnection(sqliteDatabase.getDatabaseConnection());
        }
    }

    public boolean releaseConnection(SqliteDatabase sqliteDatabase, Connection connection) {
        try {
            connection.close();
            return true;
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
        if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
            fieldQuery.append(" DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))");
        }

        // Nullable
        if (field.getNullable().trim().equalsIgnoreCase("n")) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<SqliteDatabase> appliesTo() {
        return SqliteDatabase.class;
    }


}
