package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class SqliteDatabase extends Database {

    public SqliteDatabase(SqliteDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }
    
    public SqliteDatabase() {
    	super();
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
        if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
            fieldQuery.append(" DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))");
        }

        // Nullable
        if (field.getNullable().trim().equalsIgnoreCase("n")) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }


}
