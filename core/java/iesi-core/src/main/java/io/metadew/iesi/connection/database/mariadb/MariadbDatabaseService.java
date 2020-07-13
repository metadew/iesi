package io.metadew.iesi.connection.database.mariadb;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.DatabaseService;
import io.metadew.iesi.connection.database.IDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class MariadbDatabaseService extends DatabaseService<MariadbDatabase> implements IDatabaseService<MariadbDatabase> {

    private static MariadbDatabaseService INSTANCE;

    private final static String keyword = "db.mariadb";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String databaseKey = "database";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static MariadbDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MariadbDatabaseService();
        }
        return INSTANCE;
    }

    private MariadbDatabaseService() {}

    @Override
    public MariadbDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        MariadbDatabaseConnection mariadbDatabaseConnection;
        if (DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            mariadbDatabaseConnection = new MariadbDatabaseConnection(
                    DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword);
            return new MariadbDatabase(mariadbDatabaseConnection);
        }

        String hostName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, portKey));
        String databaseName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, databaseKey);

        mariadbDatabaseConnection = new MariadbDatabaseConnection(hostName,
                port,
                databaseName,
                userName,
                userPassword);
        return new MariadbDatabase(mariadbDatabaseConnection);
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(MariadbDatabase mariadbDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(MariadbDatabase mariadbDatabase, String pattern) {
        return null;
    }

    @Override
    public String createQueryExtras(MariadbDatabase mariadbDatabase) {
        return null;
    }

    @Override
    public boolean addComments(MariadbDatabase mariadbDatabase) {
        return false;
    }

    @Override
    public String toQueryString(MariadbDatabase mariadbDatabase, MetadataField field) {
        return null;
    }

    @Override
    public Class<MariadbDatabase> appliesTo() {
        return MariadbDatabase.class;
    }


}
