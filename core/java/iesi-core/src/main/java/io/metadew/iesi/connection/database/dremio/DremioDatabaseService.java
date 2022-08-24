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

    public synchronized static DremioDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DremioDatabaseService();
        }
        return INSTANCE;
    }

    private DremioDatabaseService() {}

    @Override
    public DremioDatabase getDatabase(Connection connection) {
        String userName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, userKey);
        String userPassword = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, schemaKey);
        DremioDatabaseConnection dremioDatabaseConnection;
        if (SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            dremioDatabaseConnection = new DremioDatabaseConnection(
                    SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new DremioDatabase(dremioDatabaseConnection, schemaName);
        }

        String hostName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection,portKey));
        String connectionMode = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, connectionModeKey);
        String clusterName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, clusterNameKey);

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
