package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class OracleDatabaseServiceImpl extends SchemaDatabaseServiceImpl<OracleDatabase> implements SchemaDatabaseService<OracleDatabase>  {

    private static OracleDatabaseServiceImpl INSTANCE;

    public synchronized static OracleDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OracleDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private OracleDatabaseServiceImpl() {}

    @Override
    public String getSystemTimestampExpression(OracleDatabase oracleDatabase) {
        return "systimestamp";
    }

    @Override
    public String getAllTablesQuery(OracleDatabase oracleDatabase, String pattern) {
        // pattern = tableNamePrefix + categoryPrefix
        return "select OWNER, TABLE_NAME from ALL_TABLES where"
                + oracleDatabase.getSchema().map(schema -> " owner = '" + schema + "' and").orElse("")
                + " TABLE_NAME like '"
                + pattern
                + "%' order by TABLE_NAME ASC";
    }

    public boolean addComments(OracleDatabase oracleDatabase) {
        return true;
    }

    public String createQueryExtras(OracleDatabase oracleDatabase) {
        return "\nLOGGING\nNOCOMPRESS\nNOCACHE\nNOPARALLEL\nMONITORING";
    }

    public String toQueryString(OracleDatabase oracleDatabase, MetadataField field) {
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

    @Override
    public Class<OracleDatabase> appliesTo() {
        return OracleDatabase.class;
    }

}
