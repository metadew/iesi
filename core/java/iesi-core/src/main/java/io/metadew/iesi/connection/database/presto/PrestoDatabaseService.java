package io.metadew.iesi.connection.database.presto;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class PrestoDatabaseService extends SchemaDatabaseService<PrestoDatabase> implements ISchemaDatabaseService<PrestoDatabase> {

    private static PrestoDatabaseService INSTANCE;

    private final static String keyword = "db.presto";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String catalogNameKey = "catalog";
    private final static String portKey = "port";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);

    public synchronized static PrestoDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrestoDatabaseService();
        }
        return INSTANCE;
    }

    private PrestoDatabaseService() {}

    @Override
    public PrestoDatabase getDatabase(Connection connection) {
        String userName = databaseHandler.getMandatoryParameterWithKey(connection, userKey);
        String userPassword = databaseHandler.getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = databaseHandler.getMandatoryParameterWithKey(connection, schemaKey);
        PrestoDatabaseConnection prestoDatabaseConnection;
        if (databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            prestoDatabaseConnection = new PrestoDatabaseConnection(
                    databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new PrestoDatabase(prestoDatabaseConnection, schemaName);
        }

        String hostName = databaseHandler.getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(databaseHandler.getMandatoryParameterWithKey(connection,portKey));
        String catalogName = databaseHandler.getMandatoryParameterWithKey(connection, catalogNameKey);

        prestoDatabaseConnection = new PrestoDatabaseConnection(
                hostName,
                port,
                catalogName,
                schemaName,
                userName,
                userPassword);
        return new PrestoDatabase(prestoDatabaseConnection, schemaName);
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(PrestoDatabase prestoDatabase) {
        return "current_timestamp";
    }

    @Override
    public String getAllTablesQuery(PrestoDatabase prestoDatabase, String pattern) {
        // TODO add catalog
        // pattern = tableNamePrefix + categoryPrefix
        return "select TABLE_SCHEM, TABLE_NAME from system.jdbc.TABLES where"
                + prestoDatabase.getSchema().map(schema -> " TABLE_SCHEM = '" + schema + "' and").orElse("")
                + " TABLE_NAME like '"
                + pattern
                + "%' order by TABLE_NAME ASC";
    }

    public boolean addComments(PrestoDatabase prestoDatabase) {
        return true;
    }

    public String createQueryExtras(PrestoDatabase prestoDatabase) {
        return null;
    }

    public String toQueryString(PrestoDatabase prestoDatabase, MetadataField field) {
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
    public Class<PrestoDatabase> appliesTo() {
        return PrestoDatabase.class;
    }


}
