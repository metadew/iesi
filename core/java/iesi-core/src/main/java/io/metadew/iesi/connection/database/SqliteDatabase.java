package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

import java.sql.Connection;
import java.sql.SQLException;

public class SqliteDatabase extends Database {

    public SqliteDatabase(SqliteDatabaseConnection databaseConnection) {
        super(databaseConnection, 0, 0);
    }

    public synchronized Connection getConnection() {
        return getDatabaseConnection().getConnection();
    }

    public boolean releaseConnection(Connection connection) {
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public String getSystemTimestampExpression() {
        return "datetime(CURRENT_TIMESTAMP, 'localtime')";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        return "select tbl_name 'TABLE_NAME', '' 'OWNER' from sqlite_master where tbl_name like '"
                + pattern
                + "%' order by tbl_name asc";
    }

    @Override
    public String createQueryExtras() {
        return "";
    }

    @Override
    public boolean addComments() {
        return false;
    }

    @Override
    public String toQueryString(MetadataField field) {
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
        if (field.isNullable()) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }


}
