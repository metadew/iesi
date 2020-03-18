package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;

public class NetezzaDatabaseServiceImpl extends SchemaDatabaseServiceImpl<NetezzaDatabase> implements SchemaDatabaseService<NetezzaDatabase>  {

    private static NetezzaDatabaseServiceImpl INSTANCE;

    public synchronized static NetezzaDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetezzaDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private NetezzaDatabaseServiceImpl() {}

    @Override
    public String getSystemTimestampExpression(NetezzaDatabase netezzaDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(NetezzaDatabase netezzaDatabase, String pattern) {
        return "select SCHEMA as \"OWNER\", TABLENAME as \"TABLE_NAME\" from _V_TABLE where"
                + netezzaDatabase.getSchema().map(schema -> " OWNER = '" + schema + "' and").orElse("")
                + " TABLENAME like '"
                + pattern
                + "%' order by TABLENAME asc";
    }

    @Override
    public String createQueryExtras(NetezzaDatabase netezzaDatabase) {
        return "";
    }

    @Override
    public boolean addComments(NetezzaDatabase netezzaDatabase) {
        return true;
    }

    @Override
    public String toQueryString(NetezzaDatabase netezzaDatabase, MetadataField field) {
        StringBuilder fieldQuery = new StringBuilder();
        // Data Types
        switch (field.getType()) {
            case "string":
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(" CHAR)");
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
    public Class<NetezzaDatabase> appliesTo() {
        return NetezzaDatabase.class;
    }
}
