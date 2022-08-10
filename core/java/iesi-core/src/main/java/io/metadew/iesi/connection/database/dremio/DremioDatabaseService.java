package io.metadew.iesi.connection.database.dremio;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class DremioDatabaseService extends SchemaDatabaseService<DremioDatabase> implements ISchemaDatabaseService<DremioDatabase> {

    private static DremioDatabaseService INSTANCE;

    private final static String keyword = "db.dremio";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String connectionModeKey = "mode";
    private final static String clusterNameKey = "cluster";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);

    public synchronized static DremioDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DremioDatabaseService();
        }
        return INSTANCE;
    }

    private DremioDatabaseService() {}

    @Override
    public DremioDatabase getDatabase(Connection connection) {
        String userName = databaseHandler.getMandatoryParameterWithKey(connection, userKey);
        String userPassword = databaseHandler.getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = databaseHandler.getMandatoryParameterWithKey(connection, schemaKey);
        DremioDatabaseConnection dremioDatabaseConnection;
        if (databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            dremioDatabaseConnection = new DremioDatabaseConnection(
                    databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new DremioDatabase(dremioDatabaseConnection, schemaName);
        }

        String hostName = databaseHandler.getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(databaseHandler.getMandatoryParameterWithKey(connection,portKey));
        String connectionMode = databaseHandler.getMandatoryParameterWithKey(connection, connectionModeKey);
        String clusterName = databaseHandler.getMandatoryParameterWithKey(connection, clusterNameKey);

        dremioDatabaseConnection = new DremioDatabaseConnection(hostName,
                port,
                connectionMode,
                clusterName,
                schemaName,
                userName,
                userPassword);
        return new DremioDatabase(dremioDatabaseConnection, schemaName);
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(DremioDatabase dremioDatabase) {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(DremioDatabase dremioDatabase, String pattern) {
        return "";
    }

    public boolean addComments(DremioDatabase dremioDatabase) {
        return true;
    }

    public String createQueryExtras(DremioDatabase dremioDatabase) {
        return null;
    }

    public String toQueryString(DremioDatabase dremioDatabase, MetadataField field) {
        StringBuilder fieldQuery = new StringBuilder();
        // Data Types
        switch (field.getType()) {
            case STRING:
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(")");
                break;
            case CLOB:
                fieldQuery.append("VARBINARY");
                break;
            case FLAG:
                fieldQuery.append("CHAR (").append(field.getLength()).append(")");
                break;
            case NUMBER:
                fieldQuery.append("BIGINT");
                break;
            case TIMESTAMP:
                fieldQuery.append("TIMESTAMP (6)");
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
    public Class<DremioDatabase> appliesTo() {
        return DremioDatabase.class;
    }

}
