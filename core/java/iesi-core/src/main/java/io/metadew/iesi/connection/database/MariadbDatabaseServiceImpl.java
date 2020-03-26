package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.mariadb.MariadbDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class MariadbDatabaseServiceImpl extends DatabaseServiceImpl<MariadbDatabase> implements DatabaseService<MariadbDatabase>  {

    private static MariadbDatabaseServiceImpl INSTANCE;

    private final static String keyword = "db.mariadb";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String databaseKey = "database";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static MariadbDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MariadbDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private MariadbDatabaseServiceImpl() {}

    @Override
    public MariadbDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        MariadbDatabaseConnection mariadbDatabaseConnection;
        if (DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            mariadbDatabaseConnection = new MariadbDatabaseConnection(
                    DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword);
            return new MariadbDatabase(mariadbDatabaseConnection);
        }

        String hostName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, portKey));
        String databaseName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, databaseKey);

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
    public String getCreateStatement(MariadbDatabase mariadbDatabase, MetadataTable table, String tableNamePrefix) {
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
