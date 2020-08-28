package io.metadew.iesi.connection.database.bigquery;

import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class BigqueryDatabaseService extends SchemaDatabaseService<BigqueryDatabase> implements ISchemaDatabaseService<BigqueryDatabase> {

    private static BigqueryDatabaseService INSTANCE;

    private static final String keyword= "db.bigquery";


    public synchronized static BigqueryDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BigqueryDatabaseService();
        }
        return INSTANCE;
    }

    private BigqueryDatabaseService() {}

    @Override
    public BigqueryDatabase getDatabase(Connection connection) {
        return null;
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(BigqueryDatabase postgresqlDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(BigqueryDatabase bigqueryDatabase, String pattern) {
        return "select table_schema as \"OWNER\", table_name as \"TABLE_NAME\" from "
                + bigqueryDatabase.getSchema().map(schema -> schema).orElse("")
                + ".information_schema.tables where"
                + " table_name like '"
                + pattern
                + "%' order by table_name asc";
    }

    @Override
    public String createQueryExtras(BigqueryDatabase bigqueryDatabase) {
        return "";
    }

    @Override
    public boolean addComments(BigqueryDatabase bigqueryDatabase) {
        return true;
    }

    //TODO complete correct data types
    @Override
    public String toQueryString(BigqueryDatabase bigqueryDatabase, MetadataField field) {
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
    public Class<BigqueryDatabase> appliesTo() {
        return BigqueryDatabase.class;
    }


}
