package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.dremio.DremioDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class DremioDatabaseServiceImpl extends SchemaDatabaseServiceImpl<DremioDatabase> implements SchemaDatabaseService<DremioDatabase>  {

    private static DremioDatabaseServiceImpl INSTANCE;

    private final static String keyword = "db.dremio";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String connectionModeKey = "mode";
    private final static String clusterNameKey = "cluster";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static DremioDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DremioDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private DremioDatabaseServiceImpl() {}

    @Override
    public DremioDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, schemaKey);
        DremioDatabaseConnection dremioDatabaseConnection;
        if (DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            dremioDatabaseConnection = new DremioDatabaseConnection(
                    DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new DremioDatabase(dremioDatabaseConnection, schemaName);
        }

        String hostName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection,portKey));
        String connectionMode = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, connectionModeKey);
        String clusterName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, clusterNameKey);

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
            case "string":
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(")");
                break;
            case "flag":
                fieldQuery.append("CHAR (").append(field.getLength()).append(")");
                break;
            case "number":
                fieldQuery.append("BIGINT");
                break;
            case "timestamp":
                fieldQuery.append("TIMESTAMP (6)");
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
    public Class<DremioDatabase> appliesTo() {
        return DremioDatabase.class;
    }

}
