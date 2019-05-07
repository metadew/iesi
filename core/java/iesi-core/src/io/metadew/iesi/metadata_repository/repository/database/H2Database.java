package io.metadew.iesi.metadata_repository.repository.database;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata_repository.repository.database.connection.H2DatabaseConnection;

import java.util.Optional;

/**
 * Database object for H2 databases
 * 
 * @author peter.billen
 *
 */
public class H2Database extends Database {

    String schema;

    public H2Database(H2DatabaseConnection databaseConnection, String schema) {
        super(databaseConnection);
        this.schema = schema;
    }

    @Override
    public String getSystemTimestampExpression() {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        // pattern = tableNamePrefix + categoryPrefix
        return "select TABLE_SCHEMA, TABLE_NAME from information_schema.TABLES where"
                + getSchema().map(schema -> " TABLE_SCHEMA = '" + schema + "' and").orElse("")
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

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

}
