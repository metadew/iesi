package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class MssqlDatabaseServiceImpl extends SchemaDatabaseServiceImpl<MssqlDatabase> implements SchemaDatabaseService<MssqlDatabase>  {

    private static MssqlDatabaseServiceImpl INSTANCE;

    public synchronized static MssqlDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MssqlDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private MssqlDatabaseServiceImpl() {}


    @Override
    public String getSystemTimestampExpression(MssqlDatabase mssqlDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(MssqlDatabase mssqlDatabase, String pattern) {
        return "";
    }

    @Override
    public String createQueryExtras(MssqlDatabase mssqlDatabase) {
        return "";
    }

    @Override
    public boolean addComments(MssqlDatabase mssqlDatabase) {
        return true;
    }

    @Override
    public String toQueryString(MssqlDatabase mssqlDatabase, MetadataField field) {
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
                fieldQuery.append("DATETIME");
                break;
        }

        // Default DtTimestamp
        if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
            fieldQuery.append(" DEFAULT GETDATE()");
        }

        // Nullable
        if (field.getNullable().trim().equalsIgnoreCase("n")) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<MssqlDatabase> appliesTo() {
        return MssqlDatabase.class;
    }

}
