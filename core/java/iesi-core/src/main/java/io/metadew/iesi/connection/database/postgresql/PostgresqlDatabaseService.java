package io.metadew.iesi.connection.database.postgresql;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.util.Optional;

public class PostgresqlDatabaseService extends SchemaDatabaseService<PostgresqlDatabase> implements ISchemaDatabaseService<PostgresqlDatabase> {

    private static final String KEYWORD = "db.postgresql";
    private static final String USER_KEY = "user";
    private static final String PASSWORD_KEY = "password";
    private static final String SCHEMA_KEY = "schema";
    private static final String CONNECTION_URL_KEY = "connectionURL";
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String DATABASE_KEY = "database";

    private static PostgresqlDatabaseService instance;


    private PostgresqlDatabaseService() {
    }

    public synchronized static PostgresqlDatabaseService getInstance() {
        if (instance == null) {
            instance = new PostgresqlDatabaseService();
        }
        return instance;
    }

    @Override
    public PostgresqlDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, USER_KEY);
        String userPassword = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, PASSWORD_KEY);
        Optional<String> schemaName = DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, SCHEMA_KEY);
        PostgresqlDatabaseConnection postgresqlDatabaseConnection;
        if (DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, CONNECTION_URL_KEY).isPresent()) {
            postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(
                    DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, CONNECTION_URL_KEY).get(),
                    userName,
                    userPassword,
                    "",
                    schemaName.orElse(null));
            return new PostgresqlDatabase(postgresqlDatabaseConnection, schemaName.orElse(null));
        }
        String hostName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, HOST_KEY);
        int port = Integer.parseInt(DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, PORT_KEY));
        String databaseName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, DATABASE_KEY);

        postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(hostName,
                port,
                databaseName,
                schemaName.orElse(null),
                userName,
                userPassword,
                "");
        return new PostgresqlDatabase(postgresqlDatabaseConnection, schemaName.orElse(null));
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
