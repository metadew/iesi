package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.presto.PrestoDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class PrestoDatabaseServiceImpl extends SchemaDatabaseServiceImpl<PrestoDatabase> implements SchemaDatabaseService<PrestoDatabase>  {

    private static PrestoDatabaseServiceImpl INSTANCE;

    private final static String keyword = "db.presto";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String catalogNameKey = "catalog";
    private final static String portKey = "port";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static PrestoDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrestoDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private PrestoDatabaseServiceImpl() {}

    @Override
    public PrestoDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, schemaKey);
        PrestoDatabaseConnection prestoDatabaseConnection;
        if (DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            prestoDatabaseConnection = new PrestoDatabaseConnection(
                    DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new PrestoDatabase(prestoDatabaseConnection, schemaName);
        }

        String hostName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection,portKey));
        String catalogName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, catalogNameKey);

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
    public Class<PrestoDatabase> appliesTo() {
        return PrestoDatabase.class;
    }


}
