package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.h2.H2DatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2EmbeddedDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2ServerDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class H2DatabaseServiceImpl extends SchemaDatabaseServiceImpl<H2Database> implements SchemaDatabaseService<H2Database>  {

    private static H2DatabaseServiceImpl INSTANCE;

    private final static String keyword = "db.h2";

    private final static String connectionUrlKey = "connectionURL";

    private final static String modeKey = "mode";
    private final static String embeddedModeKey = "embedded";
    private final static String serverModeKey = "server";
    private final static String memoryModeKey = "memory";
    private final static String fileKey = "file";
    private final static String databaseKey = "database";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static H2DatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new H2DatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private H2DatabaseServiceImpl() {}

    public H2Database getDatabase(Connection connection) {
        String userName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, schemaKey);
        H2DatabaseConnection h2DatabaseConnection;
        if (DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            h2DatabaseConnection = new H2DatabaseConnection(
                    DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new H2Database(h2DatabaseConnection, schemaName);
        }
        String mode = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, modeKey);
        switch (mode) {
            case embeddedModeKey:
                h2DatabaseConnection = new H2EmbeddedDatabaseConnection(
                        DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, fileKey),
                        userName,
                        userPassword,
                        schemaName);
                return new H2Database(h2DatabaseConnection, schemaName);
            case serverModeKey:
                String host = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, hostKey);
                int port = Integer.parseInt(DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, portKey));
                h2DatabaseConnection = new H2ServerDatabaseConnection(
                        host,
                        port,
                        DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, fileKey),
                        userName,
                        userPassword,
                        schemaName);
                return new H2Database(h2DatabaseConnection, schemaName);
            case memoryModeKey:
                h2DatabaseConnection = new H2MemoryDatabaseConnection(
                        DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, databaseKey),
                        userName,
                        userPassword,
                        schemaName);
                return new H2Database(h2DatabaseConnection, schemaName);
            default:
                throw new RuntimeException("H2 database " + connection + " does not know mode '" + mode + "'");
        }
    }

    @Override
    public String getSystemTimestampExpression(H2Database h2Database) {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(H2Database h2Database, String pattern) {
        // pattern = tableNamePrefix + categoryPrefix
        return "select TABLE_SCHEMA, TABLE_NAME from information_schema.TABLES where"
                + h2Database.getSchema().map(schema -> " TABLE_SCHEMA = '" + schema + "' and").orElse(" TABLE_SCHEMA = 'PUBLIC' and")
                + " TABLE_NAME like '"
                + pattern
                + "%' order by TABLE_NAME ASC";
    }

    public boolean addComments(H2Database h2Database) {
        return true;
    }

    public String createQueryExtras(H2Database h2Database) {
        return "";
    }

    public String toQueryString(H2Database h2Database, MetadataField field) {
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
    public Class<H2Database> appliesTo() {
        return H2Database.class;
    }


    @Override
    public String keyword() {
        return keyword;
    }

    public void shutdown(H2Database h2Database) {
        executeUpdate(h2Database, "drop all objects delete files");
        super.shutdown(h2Database);
    }


}
