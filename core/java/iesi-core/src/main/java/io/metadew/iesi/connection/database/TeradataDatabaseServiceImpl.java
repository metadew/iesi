package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.teradata.TeradataDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class TeradataDatabaseServiceImpl extends DatabaseServiceImpl<TeradataDatabase> implements DatabaseService<TeradataDatabase>  {

    private static TeradataDatabaseServiceImpl INSTANCE;

    private final static String keyword = "db.teradata";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String databaseKey = "database";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static TeradataDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TeradataDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private TeradataDatabaseServiceImpl() {}

    @Override
    public TeradataDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        TeradataDatabaseConnection teradataDatabaseConnection;
        if (DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            teradataDatabaseConnection = new TeradataDatabaseConnection(
                    DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword);
            return new TeradataDatabase(teradataDatabaseConnection);
        }

        String hostName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, portKey));
        String databaseName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, databaseKey);

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
