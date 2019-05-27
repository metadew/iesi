package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.PrestoDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

/**
 * Database object for PrestoSQL
 *
 * @author peter.billen
 */
public class PrestoDatabase extends SchemaDatabase {


    public PrestoDatabase(PrestoDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    @Override
    public String getSystemTimestampExpression() {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        // TODO add catalog
        // pattern = tableNamePrefix + categoryPrefix
        return "select TABLE_SCHEM, TABLE_NAME from system.jdbc.TABLES where"
                + getSchema().map(schema -> " TABLE_SCHEM = '" + schema + "' and").orElse("")
                + " TABLE_NAME like '"
                + pattern
                + "%' order by TABLE_NAME ASC";
    }

    public boolean addComments() {
        return true;
    }

    public String createQueryExtras() {
        return null;
    }

    public String toQueryString(MetadataField field) {
        //TODO to be reviewed
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
