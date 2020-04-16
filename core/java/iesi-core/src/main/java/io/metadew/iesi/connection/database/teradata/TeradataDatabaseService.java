package io.metadew.iesi.connection.database.teradata;

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

    public synchronized static TeradataDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeradataDatabaseService();
        }
        return INSTANCE;
    }

    private TeradataDatabaseService() {}

    @Override
    public TeradataDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        TeradataDatabaseConnection teradataDatabaseConnection;
        if (DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            teradataDatabaseConnection = new TeradataDatabaseConnection(
                    DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword);
            return new TeradataDatabase(teradataDatabaseConnection);
        }

        String hostName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, portKey));
        String databaseName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, databaseKey);

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
