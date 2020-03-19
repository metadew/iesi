package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class DrillDatabaseServiceImpl extends SchemaDatabaseServiceImpl<DrillDatabase> implements SchemaDatabaseService<DrillDatabase>  {


    private static DrillDatabaseServiceImpl INSTANCE;

    public synchronized static DrillDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DrillDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private DrillDatabaseServiceImpl() {}
    
    @Override
    public String getSystemTimestampExpression(DrillDatabase drillDatabase) {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(DrillDatabase drillDatabase, String pattern) {
        return "";
    }

    public boolean addComments(DrillDatabase drillDatabase) {
        return true;
    }

    public String createQueryExtras(DrillDatabase drillDatabase) {
        return null;
    }

    public String toQueryString(DrillDatabase drillDatabase, MetadataField field) {
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
    public Class<DrillDatabase> appliesTo() {
        return DrillDatabase.class;
    }
}
