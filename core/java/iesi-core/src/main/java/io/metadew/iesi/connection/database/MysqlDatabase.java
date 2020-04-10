package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.mysql.MysqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

import java.util.List;
import java.util.stream.Collectors;

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
        return "select table_schema as \"OWNER\", table_name as \"TABLE_NAME\" from information_schema.tables where"
                + getSchema().map(schema -> " table_schema = '" + schema + "' and").orElse("")
                + " table_name like '"
                + pattern
                + "%' order by table_name asc";
    }
    @Override
    public String toFieldName(MetadataField field) {
        StringBuilder result = new StringBuilder();
        result.append("`");
        result.append(field.getName());
        result.append("`");
        return result.toString();
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
    public String toPrimaryKeyConstraint(MetadataTable metadataTable, List<MetadataField> primaryKeyMetadataFields) {
        return "PRIMARY KEY (" + primaryKeyMetadataFields.stream().map(MetadataField::getName).collect(Collectors.joining(", ")) + ")";
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
