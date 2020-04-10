package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.db2.Db2DatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

import java.util.List;

public class Db2Database extends SchemaDatabase {

    public Db2Database(Db2DatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    @Override
    public String getSystemTimestampExpression() {
        return "CURRENT TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        return "";
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
                fieldQuery.append("DATE");
                break;
        }

        // Default DtTimestamp
        if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
            fieldQuery.append(" DEFAULT CURRENT TIMESTAMP");
        }

        // Nullable
        if (field.getNullable().trim().equalsIgnoreCase("n")) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public String toPrimaryKeyConstraint(MetadataTable metadataTable, List<MetadataField> primaryKeyMetadataFields) {return ""; }

    @Override
    public String toFieldName(MetadataField field) {
        StringBuilder result = new StringBuilder();
        result.append(field.getName());
        return result.toString();
    }
}
