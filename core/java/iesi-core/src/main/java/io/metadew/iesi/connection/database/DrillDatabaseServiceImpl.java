package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.drill.DrillDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class DrillDatabaseServiceImpl extends SchemaDatabaseServiceImpl<DrillDatabase> implements SchemaDatabaseService<DrillDatabase>  {

    private static DrillDatabaseServiceImpl INSTANCE;

    private final static String keyword = "db.drill";

    private final static String connectionUrlKey = "connectionURL";

    private final static String connectionModeKey = "mode";
    private final static String clusterNamesKey = "cluster";
    private final static String directoryNameKey = "directory";
    private final static String clusterIdKey = "clusterId";
    private final static String schemaKey = "schema";
    private final static String triesParameterKey = "tries";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static DrillDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DrillDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private DrillDatabaseServiceImpl() {}

    @Override
    public DrillDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, schemaKey);
        DrillDatabaseConnection drillDatabaseConnection;
        if (DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            drillDatabaseConnection = new DrillDatabaseConnection(
                    DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new DrillDatabase(drillDatabaseConnection, schemaName);
        }

        String connectionMode = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, connectionModeKey);
        String clusterName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, clusterNamesKey);
        String directoryName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, directoryNameKey);
        String clusterId = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, clusterIdKey);
        String triesParameter = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, triesParameterKey);

        drillDatabaseConnection = new DrillDatabaseConnection(connectionMode,
                clusterName,
                directoryName,
                clusterId,
                schemaName,
                triesParameter,
                userName,
                userPassword);
        return new DrillDatabase(drillDatabaseConnection, schemaName);
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(DrillDatabase drillDatabase) {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(DrillDatabase drillDatabase, String pattern) {
        return "";
    }

    public boolean addComments(DrillDatabase drillDatabase) {
        return true;
    }

    public String createQueryExtras(DrillDatabase drillDatabase) {
        return null;
    }

    public String toQueryString(DrillDatabase drillDatabase, MetadataField field) {
        //TODO to be reviewed
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
    public Class<DrillDatabase> appliesTo() {
        return DrillDatabase.class;
    }
}
