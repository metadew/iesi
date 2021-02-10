package io.metadew.iesi.connection.database.mysql;

import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class MysqlDatabaseService extends SchemaDatabaseService<MysqlDatabase> implements ISchemaDatabaseService<MysqlDatabase> {

    private static MysqlDatabaseService INSTANCE;

    private static final String keyword= "db.mysql";

    public synchronized static MysqlDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MysqlDatabaseService();
        }
        return INSTANCE;
    }

    private MysqlDatabaseService() {}

    @Override
    public MysqlDatabase getDatabase(Connection connection) {
        return null;
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(MysqlDatabase mysqlDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(MysqlDatabase mysqlDatabase, String pattern) {
        return "select table_schema as \"OWNER\", table_name as \"TABLE_NAME\" from information_schema.tables where"
                + mysqlDatabase.getSchema().map(schema -> " table_schema = '" + schema + "' and").orElse("")
                + " table_name like '"
                + pattern
                + "%' order by table_name asc";
    }

    @Override
    public String createQueryExtras(MysqlDatabase mysqlDatabase) {
        return "";
    }

    @Override
    public boolean addComments(MysqlDatabase mysqlDatabase) {
        return false;
    }

    @Override
    public String toQueryString(MysqlDatabase mysqlDatabase, MetadataField field) {
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

        // Nullable
        if (!field.isNullable()) {
            fieldQuery.append(" NOT NULL ");
        } else {
            fieldQuery.append(" NULL ");
        }

        // Default DtTimestamp
        if (field.isDefaultTimestamp()) {
            fieldQuery.append(" DEFAULT CURRENT_TIMESTAMP");
        }

        return fieldQuery.toString();
    }

    @Override
    public Class<MysqlDatabase> appliesTo() {
        return MysqlDatabase.class;
    }


    public String fieldNameToQueryString(MysqlDatabase database, String fieldName) {
        return "`" + fieldName + "`";
    }

}
