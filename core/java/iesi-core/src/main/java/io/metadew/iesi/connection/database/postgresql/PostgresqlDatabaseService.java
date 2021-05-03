package io.metadew.iesi.connection.database.postgresql;

import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class PostgresqlDatabaseService extends SchemaDatabaseService<PostgresqlDatabase> implements ISchemaDatabaseService<PostgresqlDatabase> {

    private static PostgresqlDatabaseService instance;

    private static final String KEYWORD = "db.postgresql";


    public synchronized static PostgresqlDatabaseService getInstance() {
        if (instance == null) {
            instance = new PostgresqlDatabaseService();
        }
        return instance;
    }

    private PostgresqlDatabaseService() {}

    @Override
    public PostgresqlDatabase getDatabase(Connection connection) {
        // TODO: create Database from connection
        return null;
    }

    @Override
    public String keyword() {
        return KEYWORD;
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
            case STRING:
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(")");
                break;
            case CLOB:
                fieldQuery.append("TEXT");
                break;
            case FLAG:
                fieldQuery.append("CHAR (").append(field.getLength()).append(")");
                break;
            case NUMBER:
                fieldQuery.append("NUMERIC");
                break;
            case TIMESTAMP:
                fieldQuery.append("TIMESTAMP");
                break;
        }

        // Default DtTimestamp
        if (field.isDefaultTimestamp()) {
            fieldQuery.append(" DEFAULT CURRENT_TIMESTAMP");
        }

        // Nullable
        if (!field.isNullable()) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<PostgresqlDatabase> appliesTo() {
        return PostgresqlDatabase.class;
    }


}
