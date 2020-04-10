package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.drill.DrillDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;

import java.util.List;

/**
 * Database object for Apache Drill
 * 
 * @author peter.billen
 *
 */
public class DrillDatabase extends SchemaDatabase {

    public DrillDatabase(DrillDatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    @Override
    public String getSystemTimestampExpression() {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(String pattern) {
        return "";
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

    @Override
    public String toPrimaryKeyConstraint(MetadataTable metadataTable, List<MetadataField> primaryKeyMetadataFields) {return ""; }

    @Override
    public String toFieldName(MetadataField field) {
        StringBuilder result = new StringBuilder();
        result.append(field.getName());
        return result.toString();
    }
}
