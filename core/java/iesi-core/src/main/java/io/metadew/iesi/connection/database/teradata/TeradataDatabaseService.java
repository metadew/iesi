package io.metadew.iesi.connection.database.teradata;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.DatabaseService;
import io.metadew.iesi.connection.database.IDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class TeradataDatabaseService extends DatabaseService<TeradataDatabase> implements IDatabaseService<TeradataDatabase> {

    private static TeradataDatabaseService INSTANCE;

    private final static String keyword = "db.teradata";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String databaseKey = "database";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);

    public synchronized static TeradataDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeradataDatabaseService();
        }
        return INSTANCE;
    }

    private TeradataDatabaseService() {}

    @Override
    public TeradataDatabase getDatabase(Connection connection) {
        String userName = databaseHandler.getMandatoryParameterWithKey(connection, userKey);
        String userPassword = databaseHandler.getMandatoryParameterWithKey(connection, passwordKey);
        TeradataDatabaseConnection teradataDatabaseConnection;
        if (databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            teradataDatabaseConnection = new TeradataDatabaseConnection(
                    databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword);
            return new TeradataDatabase(teradataDatabaseConnection);
        }

        String hostName = databaseHandler.getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(databaseHandler.getMandatoryParameterWithKey(connection, portKey));
        String databaseName = databaseHandler.getMandatoryParameterWithKey(connection, databaseKey);

        teradataDatabaseConnection = new TeradataDatabaseConnection(hostName, port, databaseName, userName, userPassword);
        return new TeradataDatabase(teradataDatabaseConnection);
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(TeradataDatabase teradataDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(TeradataDatabase teradataDatabase, String pattern) {
        return null;
    }

    @Override
    public String createQueryExtras(TeradataDatabase teradataDatabase) {
        return "";
    }

    @Override
    public boolean addComments(TeradataDatabase teradataDatabase) {
        return false;
    }

    @Override
    public String toQueryString(TeradataDatabase teradataDatabase, MetadataField field) {
        return "";
    }

    @Override
    public Class<TeradataDatabase> appliesTo() {
        return TeradataDatabase.class;
    }

}
