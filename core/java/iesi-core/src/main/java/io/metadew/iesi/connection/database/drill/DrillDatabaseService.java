package io.metadew.iesi.connection.database.drill;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class DrillDatabaseService extends SchemaDatabaseService<DrillDatabase> implements ISchemaDatabaseService<DrillDatabase> {

    private static DrillDatabaseService INSTANCE;

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

    public synchronized static DrillDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DrillDatabaseService();
        }
        return INSTANCE;
    }

    private DrillDatabaseService() {}

    @Override
    public DrillDatabase getDatabase(Connection connection) {
        String userName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, userKey);
        String userPassword = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, schemaKey);
        DrillDatabaseConnection drillDatabaseConnection;
        if (SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            drillDatabaseConnection = new DrillDatabaseConnection(
                    SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new DrillDatabase(drillDatabaseConnection, schemaName);
        }

        String connectionMode = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, connectionModeKey);
        String clusterName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, clusterNamesKey);
        String directoryName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, directoryNameKey);
        String clusterId = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, clusterIdKey);
        String triesParameter = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, triesParameterKey);

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
    public Class<DrillDatabase> appliesTo() {
        return DrillDatabase.class;
    }
}
