package io.metadew.iesi.connection.database;

import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class PostgresqlDatabaseServiceImpl extends SchemaDatabaseServiceImpl<PostgresqlDatabase> implements SchemaDatabaseService<PostgresqlDatabase>  {

    private static PostgresqlDatabaseServiceImpl INSTANCE;

    private static final String keyword= "db.postgresql";


    public synchronized static PostgresqlDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PostgresqlDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private PostgresqlDatabaseServiceImpl() {}

    @Override
    public PostgresqlDatabase getDatabase(Connection connection) {
        return null;
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(PostgresqlDatabase postgresqlDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(PostgresqlDatabase postgresqlDatabase, String pattern) {
        return "select table_schema as \"OWNER\", table_name as \"TABLE_NAME\" from information_schema.tables where"
                + postgresqlDatabase.getSchema().map(schema -> " table_schema = '" + schema + "' and").orElse("")
                + " table_name like '"
                + pattern
                + "%' order by table_name asc";
    }

    @Override
    public String createQueryExtras(PostgresqlDatabase postgresqlDatabase) {
        return "";
    }

    @Override
    public boolean addComments(PostgresqlDatabase postgresqlDatabase) {
        return true;
    }

    @Override
    public String toQueryString(PostgresqlDatabase postgresqlDatabase, MetadataField field) {
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
    public Class<PostgresqlDatabase> appliesTo() {
        return PostgresqlDatabase.class;
    }


}
