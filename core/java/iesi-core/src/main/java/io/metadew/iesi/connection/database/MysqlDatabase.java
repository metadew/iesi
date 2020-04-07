package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.mysql.MysqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class MysqlDatabase extends SchemaDatabase {


    public MysqlDatabase(MysqlDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    @Override
    public String getSystemTimestampExpression() {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        return null;
    }

    @Override
    public String toQueryString(MetadataField field) {
        StringBuilder fieldQuery = new StringBuilder();
        // Data Types
        switch (field.getType()) {
            case "string":
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(")");
                break;
            case "flag":
                fieldQuery.append("CHAR (").append(field.getLength()).append(")");
                break;
            case "number":
                fieldQuery.append("NUMERIC");
                break;
            case "timestamp":
                fieldQuery.append("TIMESTAMP");
                break;
        }

        // Default DtTimestamp
        if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
            fieldQuery.append(" DEFAULT CURRENT_TIMESTAMP");
        }

        // Nullable
        if (field.getNullable().trim().equalsIgnoreCase("n")) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public String createQueryExtras() {
        return "";
    }

    @Override
    public boolean addComments() {
        return false;
    }

}
