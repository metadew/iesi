package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class PrestoDatabaseServiceImpl extends SchemaDatabaseServiceImpl<PrestoDatabase> implements SchemaDatabaseService<PrestoDatabase>  {

    private static PrestoDatabaseServiceImpl INSTANCE;

    public synchronized static PrestoDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrestoDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private PrestoDatabaseServiceImpl() {}


    @Override
    public String getSystemTimestampExpression(PrestoDatabase prestoDatabase) {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(PrestoDatabase prestoDatabase, String pattern) {
        // TODO add catalog
        // pattern = tableNamePrefix + categoryPrefix
        return "select TABLE_SCHEM, TABLE_NAME from system.jdbc.TABLES where"
                + prestoDatabase.getSchema().map(schema -> " TABLE_SCHEM = '" + schema + "' and").orElse("")
                + " TABLE_NAME like '"
                + pattern
                + "%' order by TABLE_NAME ASC";
    }

    public boolean addComments(PrestoDatabase prestoDatabase) {
        return true;
    }

    public String createQueryExtras(PrestoDatabase prestoDatabase) {
        return null;
    }

    public String toQueryString(PrestoDatabase prestoDatabase, MetadataField field) {
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
    public Class<PrestoDatabase> appliesTo() {
        return PrestoDatabase.class;
    }


}
