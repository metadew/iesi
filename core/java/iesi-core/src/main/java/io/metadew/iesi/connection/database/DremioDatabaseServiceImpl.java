package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class DremioDatabaseServiceImpl extends SchemaDatabaseServiceImpl<DremioDatabase> implements SchemaDatabaseService<DremioDatabase>  {

    private static DremioDatabaseServiceImpl INSTANCE;

    public synchronized static DremioDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DremioDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private DremioDatabaseServiceImpl() {}

    @Override
    public String getSystemTimestampExpression(DremioDatabase dremioDatabase) {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(DremioDatabase dremioDatabase, String pattern) {
        return "";
    }

    public boolean addComments(DremioDatabase dremioDatabase) {
        return true;
    }

    public String createQueryExtras(DremioDatabase dremioDatabase) {
        return null;
    }

    public String toQueryString(DremioDatabase dremioDatabase, MetadataField field) {
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
    public Class<DremioDatabase> appliesTo() {
        return DremioDatabase.class;
    }

}
