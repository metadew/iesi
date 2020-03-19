package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class H2DatabaseServiceImpl extends SchemaDatabaseServiceImpl<H2Database> implements SchemaDatabaseService<H2Database>  {

    private static H2DatabaseServiceImpl INSTANCE;

    public synchronized static H2DatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new H2DatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private H2DatabaseServiceImpl() {}


    @Override
    public String getSystemTimestampExpression(H2Database h2Database) {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(H2Database h2Database, String pattern) {
        // pattern = tableNamePrefix + categoryPrefix
        return "select TABLE_SCHEMA, TABLE_NAME from information_schema.TABLES where"
                + h2Database.getSchema().map(schema -> " TABLE_SCHEMA = '" + schema + "' and").orElse(" TABLE_SCHEMA = 'PUBLIC' and")
                + " TABLE_NAME like '"
                + pattern
                + "%' order by TABLE_NAME ASC";
    }

    public boolean addComments(H2Database h2Database) {
        return true;
    }

    public String createQueryExtras(H2Database h2Database) {
        return "";
    }

    public String toQueryString(H2Database h2Database, MetadataField field) {
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
    public Class<H2Database> appliesTo() {
        return H2Database.class;
    }

    public void shutdown(H2Database h2Database) {
        executeUpdate(h2Database, "drop all objects delete files");
        super.shutdown(h2Database);
    }


}
