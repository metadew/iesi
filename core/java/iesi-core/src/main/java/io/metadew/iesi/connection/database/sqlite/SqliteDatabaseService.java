package io.metadew.iesi.connection.database.sqlite;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.DatabaseService;
import io.metadew.iesi.connection.database.IDatabaseService;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandler;
import io.metadew.iesi.metadata.definition.MetadataField;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SqliteDatabaseService extends DatabaseService<SqliteDatabase> implements IDatabaseService<SqliteDatabase> {

    private static SqliteDatabaseService instance;

    private static final String KEYWORD = "db.sqlite";
    private static final String FILE_PATH = "filePath";
    private static final String FILE_NAME = "fileName";


    public static synchronized SqliteDatabaseService getInstance() {
        if (instance == null) {
            instance = new SqliteDatabaseService();
        }
        return instance;
    }

    private SqliteDatabaseService() {
    }

    @Override
    public SqliteDatabase getDatabase(io.metadew.iesi.metadata.definition.connection.Connection connection) {
        return new SqliteDatabase(new SqliteDatabaseConnection(
                DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, FILE_PATH) +
                        File.separator +
                        DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, FILE_NAME),
                ""
        ));
    }

    @Override
    public String keyword() {
        return KEYWORD;
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
            case STRING:
            case FLAG:
            case CLOB:
            case TIMESTAMP:
                fieldQuery.append("TEXT");
                break;
            case NUMBER:
                fieldQuery.append("NUMERIC");
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
