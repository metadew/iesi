package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class Db2DatabaseServiceImpl extends SchemaDatabaseServiceImpl<Db2Database> implements SchemaDatabaseService<Db2Database> {

    private static Db2DatabaseServiceImpl INSTANCE;

    public synchronized static Db2DatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Db2DatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private Db2DatabaseServiceImpl() {}

    @Override
    public String getSystemTimestampExpression(Db2Database db2Database) {
        return "CURRENT TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(Db2Database db2Database, String pattern) {
        return "";
    }

    @Override
    public String createQueryExtras(Db2Database db2Database) {
        return "";
    }

    @Override
    public boolean addComments(Db2Database db2Database) {
        return true;
    }

    @Override
    public String toQueryString(Db2Database db2Database, MetadataField field) {
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
    public Class<Db2Database> appliesTo() {
        return Db2Database.class;
    }

}
