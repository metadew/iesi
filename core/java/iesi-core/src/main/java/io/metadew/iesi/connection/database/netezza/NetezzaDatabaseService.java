package io.metadew.iesi.connection.database.netezza;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.util.Optional;

public class NetezzaDatabaseService extends SchemaDatabaseService<NetezzaDatabase> implements ISchemaDatabaseService<NetezzaDatabase> {

    private static final String KEYWORD = "db.netezza";
    private final static String USER_KEY = "user";
    private final static String PASSWORD_KEY = "password";
    private final static String SCHEMA_KEY = "schema";
    private final static String CONNECTION_URL_KEY = "connectionURL";
    private final static String HOST_KEY = "host";
    private final static String PORT_KEY = "port";
    private final static String DATABASE_KEY = "database";
    private static NetezzaDatabaseService INSTANCE;

    private NetezzaDatabaseService() {
    }

    public synchronized static NetezzaDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetezzaDatabaseService();
        }
        return INSTANCE;
    }

    @Override
    public NetezzaDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, USER_KEY);
        String userPassword = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, PASSWORD_KEY);
        Optional<String> schemaName = DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, SCHEMA_KEY);
        NetezzaDatabaseConnection netezzaDatabaseConnection;
        if (DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, CONNECTION_URL_KEY).isPresent()) {
            netezzaDatabaseConnection = new NetezzaDatabaseConnection(
                    DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, CONNECTION_URL_KEY).get(),
                    userName,
                    userPassword,
                    "",
                    schemaName.orElse(null)
            );
            return new NetezzaDatabase(netezzaDatabaseConnection, schemaName.orElse(null));
        }
        String hostName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, HOST_KEY);
        int port = Integer.parseInt(DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, PORT_KEY));
        String databaseName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, DATABASE_KEY);

        netezzaDatabaseConnection = new NetezzaDatabaseConnection(
                hostName,
                port,
                databaseName,
                userName,
                userPassword,
                "",
                schemaName.orElse(null)
        );
        return new NetezzaDatabase(netezzaDatabaseConnection, schemaName.orElse(null));
    }

    @Override
    public String keyword() {
        return KEYWORD;
    }

    @Override
    public String getSystemTimestampExpression(NetezzaDatabase netezzaDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(NetezzaDatabase netezzaDatabase, String pattern) {
        return "select SCHEMA as \"OWNER\", TABLENAME as \"TABLE_NAME\" from _V_TABLE where"
                + netezzaDatabase.getSchema().map(schema -> " OWNER = '" + schema + "' and").orElse("")
                + " TABLENAME like '"
                + pattern
                + "%' order by TABLENAME asc";
    }

    @Override
    public String createQueryExtras(NetezzaDatabase netezzaDatabase) {
        return "";
    }

    @Override
    public boolean addComments(NetezzaDatabase netezzaDatabase) {
        return true;
    }

    @Override
    public String toQueryString(NetezzaDatabase netezzaDatabase, MetadataField field) {
        StringBuilder fieldQuery = new StringBuilder();
        // Data Types
        switch (field.getType()) {
            case STRING:
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(" CHAR)");
                break;
            case CLOB:
                fieldQuery.append("VARCHAR (64000 CHAR)");
                break;
            case FLAG:
                fieldQuery.append("CHAR (").append(field.getLength()).append(")");
                break;
            case NUMBER:
                fieldQuery.append("NUMERIC");
                break;
            case TIMESTAMP:
                fieldQuery.append("TIMESTAMP");
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
    public Class<NetezzaDatabase> appliesTo() {
        return NetezzaDatabase.class;
    }
}
