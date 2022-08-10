package io.metadew.iesi.connection.database.bigquery;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandler;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class BigqueryDatabaseService extends SchemaDatabaseService<BigqueryDatabase> implements ISchemaDatabaseService<BigqueryDatabase> {

    private static BigqueryDatabaseService INSTANCE;

    private static final String keyword= "db.bigquery";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String projectKey = "project";
    private final static String authModeKey = "authMode";
    private final static String serviceAccountKey = "serviceAccount";
    private final static String keyPathKey = "keyPath";
    private final static String accessTokenKey = "accessToken";
    private final static String refreshTokenKey = "refreshToken";
    private final static String clientIdKey = "clientId";
    private final static String clientSecretKey = "clientSecret";
    private final static String schemaKey = "dataset";

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);

    public synchronized static BigqueryDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BigqueryDatabaseService();
        }
        return INSTANCE;
    }

    private BigqueryDatabaseService() {}

    @Override
    public java.sql.Connection getConnection(BigqueryDatabase bigqueryDatabase) {
        return DatabaseConnectionHandler.getInstance().getConnection(bigqueryDatabase.getDatabaseConnection());
    }

    @Override
    public BigqueryDatabase getDatabase(Connection connection) {
        String schemaName = databaseHandler.getMandatoryParameterWithKey(connection, schemaKey);

        BigqueryDatabaseConnection bigqueryDatabaseConnection;

        //ConnectionUrl has been provided
        if (databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            bigqueryDatabaseConnection = new BigqueryDatabaseConnection(
                    databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).get());
            return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
        }

        //Individual parameters have been provided
        String host = databaseHandler.getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(databaseHandler.getMandatoryParameterWithKey(connection, portKey));
        String project = databaseHandler.getMandatoryParameterWithKey(connection, projectKey);
        String authMode = databaseHandler.getMandatoryParameterWithKey(connection, authModeKey);

        switch (authMode) {
            case "service":
                bigqueryDatabaseConnection = new ServiceBigqueryDatabaseConnection(
                                host,
                                port,
                                project,
                                databaseHandler.getMandatoryParameterWithKey(connection, serviceAccountKey),
                                databaseHandler.getMandatoryParameterWithKey(connection, keyPathKey));
                return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
            case "user":
                bigqueryDatabaseConnection = new UserBigqueryDatabaseConnection(
                                host,
                                port,
                                project);
                return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
            case "token":
                bigqueryDatabaseConnection = new TokenBigqueryDatabaseConnection(
                                host,
                                port,
                                project,
                                databaseHandler.getOptionalParameterWithKey(connection, accessTokenKey).orElse(null),
                                databaseHandler.getOptionalParameterWithKey(connection, refreshTokenKey).orElse(null),
                                databaseHandler.getOptionalParameterWithKey(connection, clientIdKey).orElse(null),
                                databaseHandler.getOptionalParameterWithKey(connection, clientSecretKey).orElse(null));
                return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
            case "default":
                bigqueryDatabaseConnection = new DefaultBigqueryDatabaseConnection(
                                host,
                                port,
                                project);
                return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
            default:
                throw new RuntimeException("Bigquery database " + connection + " does not know authMode '" + authMode + "'");
        }
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(BigqueryDatabase bigqueryDatabase) {
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
            case STRING:
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(")");
                break;
            case CLOB:
                fieldQuery.append("STRING");
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
    public Class<BigqueryDatabase> appliesTo() {
        return BigqueryDatabase.class;
    }


}
