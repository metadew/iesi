package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.postgresql.PostgresqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class PostgresqlDatabase extends SchemaDatabase {

    public PostgresqlDatabase(PostgresqlDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    public PostgresqlDatabase(PostgresqlDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }
    
    @Override
    public String getSystemTimestampExpression() {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        return "select table_schema as \"OWNER\", table_name as \"TABLE_NAME\" from information_schema.tables where"
                + getSchema().map(schema -> " table_schema = '" + schema + "' and").orElse("")
                + " table_name like '"
                + pattern
                + "%' order by table_name asc";
    }

    @Override
    public String createQueryExtras() {
        return "";
    }

    @Override
    public boolean addComments() {
        return true;
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
        if (field.isDefaultTimestamp()) {
            fieldQuery.append(" DEFAULT CURRENT_TIMESTAMP");
        }

        // Nullable
        if (!field.isNullable()) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

}

