package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.H2DatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

/**
 * Database object for H2 databases
 *
 * @author peter.billen
 */
public class H2Database extends SchemaDatabase {


    public H2Database(H2DatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    @Override
    public String getSystemTimestampExpression() {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        // pattern = tableNamePrefix + categoryPrefix
        return "select TABLE_SCHEMA, TABLE_NAME from information_schema.TABLES where"
                + getSchema().map(schema -> " TABLE_SCHEMA = '" + schema + "' and").orElse(" TABLE_SCHEMA = 'PUBLIC' and")
                + " TABLE_NAME like '"
                + pattern
                + "%' order by TABLE_NAME ASC";
    }

    public boolean addComments() {
        return true;
    }

    public String createQueryExtras() {
        return "";
    }

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
                fieldQuery.append("BIGINT");
                break;
            case "timestamp":
                fieldQuery.append("TIMESTAMP (6)");
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

}
