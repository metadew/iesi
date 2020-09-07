package io.metadew.iesi.connection.database.bigquery;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class BigqueryDatabaseService extends SchemaDatabaseService<BigqueryDatabase> implements ISchemaDatabaseService<BigqueryDatabase> {

    private static BigqueryDatabaseService INSTANCE;

    private static final String keyword= "db.bigquery";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String projectKey = "project";
    private final static String authValueKey = "authValue";
    private final static String serviceAccountKey = "serviceAccount";
    private final static String keyPathKey = "keyPath";
    private final static String accessTokenKey = "accessToken";
    private final static String refreshTokenKey = "refreshToken";
    private final static String clientIdKey = "clientId";
    private final static String clientSecretKey = "clientSecret";
    private final static String schemaKey = "dataset";

    public synchronized static BigqueryDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BigqueryDatabaseService();
        }
        return INSTANCE;
    }

    private BigqueryDatabaseService() {}

    @Override
    public BigqueryDatabase getDatabase(Connection connection) {
        String schemaName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, schemaKey);

        BigqueryDatabaseConnection bigqueryDatabaseConnection;

        //ConnectionUrl has been provided
        if (DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            bigqueryDatabaseConnection = new BigqueryDatabaseConnection(
                    DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get());
            return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
        }

        //Individual parameters have been provided
        String host = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, portKey));
        String project = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, projectKey);
        int authValue = Integer.parseInt(DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, authValueKey));

        switch (authValue) {
            case 0:
                bigqueryDatabaseConnection = new BigqueryDatabaseConnection(
                        getConnectionUrlServiceAccount(
                                host,
                                port,
                                project,
                                authValue,
                                DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, serviceAccountKey),
                                DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, keyPathKey)));
                return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
            case 1:
                bigqueryDatabaseConnection = new BigqueryDatabaseConnection(
                        getConnectionUrlUserAccount(
                                host,
                                port,
                                project,
                                authValue));
                return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
            case 2:
                bigqueryDatabaseConnection = new BigqueryDatabaseConnection(
                        getConnectionUrlWithTokens (
                                host,
                                port,
                                project,
                                authValue,
                                DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, accessTokenKey),
                                DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, refreshTokenKey),
                                DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, clientIdKey),
                                DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, clientSecretKey)));
                return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
            case 3:
                bigqueryDatabaseConnection = new BigqueryDatabaseConnection(
                        getConnectionUrlDefaultCredentials(
                                host,
                                port,
                                project,
                                authValue));
                return new BigqueryDatabase(bigqueryDatabaseConnection, schemaName);
            default:
                throw new RuntimeException("Bigquery database " + connection + " does not know authValue '" + authValue + "'");
        }
    }

    private static String getConnectionUrlServiceAccount(String hostName, int portNumber, String project, int authValue, String serviceAccount, String keyPath) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:bigquery://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }
        connectionUrl.append(";ProjectId=");
        connectionUrl.append(project);
        connectionUrl.append(";OAuthType=");
        connectionUrl.append(authValue);
        connectionUrl.append(";OAuthServiceAcctEmail=");
        connectionUrl.append(serviceAccount);
        connectionUrl.append(";OAuthPvtKeyPath=");
        connectionUrl.append(keyPath);
        connectionUrl.append(";");

        return connectionUrl.toString();
    }

    private static String getConnectionUrlUserAccount(String hostName, int portNumber, String project, int authValue) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:bigquery://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }
        connectionUrl.append(";ProjectId=");
        connectionUrl.append(project);
        connectionUrl.append(";OAuthType=");
        connectionUrl.append(authValue);
        connectionUrl.append(";");

        return connectionUrl.toString();
    }

    private static String getConnectionUrlWithTokens(String hostName, int portNumber, String project, int authValue, String accessToken, String refreshToken, String clientId, String clientSecret) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:bigquery://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }
        connectionUrl.append(";ProjectId=");
        connectionUrl.append(project);
        connectionUrl.append(";OAuthType=");
        connectionUrl.append(authValue);
        if (!accessToken.isEmpty()) {
            connectionUrl.append(";OAuthAccessToken=");
            connectionUrl.append(accessToken);
        }
        if (!refreshToken.isEmpty()) {
            connectionUrl.append(";OAuthRefreshToken=");
            connectionUrl.append(refreshToken);
        }
        if (!clientId.isEmpty()) {
            connectionUrl.append(";OAuthClientId=");
            connectionUrl.append(clientId);
        }
        if (!clientSecret.isEmpty()) {
            connectionUrl.append(";OAuthClientSecret=");
            connectionUrl.append(clientSecret);
        }
        connectionUrl.append(";");

        return connectionUrl.toString();
    }

    private static String getConnectionUrlDefaultCredentials(String hostName, int portNumber, String project, int authValue) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:bigquery://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }
        connectionUrl.append(";ProjectId=");
        connectionUrl.append(project);
        connectionUrl.append(";OAuthType=");
        connectionUrl.append(authValue);
        connectionUrl.append(";");

        return connectionUrl.toString();
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
