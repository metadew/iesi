package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.OracleDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;

public class OracleDatabase extends SchemaDatabase {

    public OracleDatabase(OracleDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    @Override
    public String getSystemTimestampExpression() {
        return "systimestamp";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        // pattern = tableNamePrefix + categoryPrefix
        return "select OWNER, TABLE_NAME from ALL_TABLES where"
                + getSchema().map(schema -> " owner = '" + schema + "' and").orElse("")
                + " TABLE_NAME like '"
                + pattern
                + "%' order by TABLE_NAME ASC";
    }

    public boolean addComments() {
        return true;
    }

    public String createQueryExtras() {
        return "\nLOGGING\nNOCOMPRESS\nNOCACHE\nNOPARALLEL\nMONITORING;\n";
    }

    public String toQueryString(MetadataField field) {
        StringBuilder fieldQuery = new StringBuilder();
        // Data Types
        switch (field.getType()) {
            case "string":
                fieldQuery.append("VARCHAR2 (").append(field.getLength()).append(" CHAR)");
                break;
            case "flag":
                fieldQuery.append("CHAR (").append(field.getLength()).append(" CHAR)");
                break;
            case "number":
                fieldQuery.append("NUMBER");
                break;
            case "timestamp":
                fieldQuery.append("TIMESTAMP (6)");
                break;
        }

        // Default DtTimestamp
        if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
            fieldQuery.append(" DEFAULT systimestamp");
        }

        // Nullable
        if (field.getNullable().trim().equalsIgnoreCase("n")) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

}
