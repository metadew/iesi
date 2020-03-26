package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.mssql.MssqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class MssqlDatabaseServiceImpl extends SchemaDatabaseServiceImpl<MssqlDatabase> implements SchemaDatabaseService<MssqlDatabase>  {

    private static MssqlDatabaseServiceImpl INSTANCE;

    private final static String keyword = "db.mssql";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String databaseKey = "database";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static MssqlDatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MssqlDatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private MssqlDatabaseServiceImpl() {}


    @Override
    public MssqlDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, schemaKey);
        MssqlDatabaseConnection mssqlDatabaseConnection;
        if ( DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            mssqlDatabaseConnection = new MssqlDatabaseConnection(
                    DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new MssqlDatabase(mssqlDatabaseConnection, schemaName);
        }

        String hostName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection,portKey));
        String databaseName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, databaseKey);

        mssqlDatabaseConnection = new MssqlDatabaseConnection(hostName,
                port,
                databaseName,
                userName,
                userPassword);
        return new MssqlDatabase(mssqlDatabaseConnection, schemaName);
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(MssqlDatabase mssqlDatabase) {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(MssqlDatabase mssqlDatabase, String pattern) {
        return "";
    }

    @Override
    public String createQueryExtras(MssqlDatabase mssqlDatabase) {
        return "";
    }

    @Override
    public boolean addComments(MssqlDatabase mssqlDatabase) {
        return true;
    }

    @Override
    public String toQueryString(MssqlDatabase mssqlDatabase, MetadataField field) {
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
                fieldQuery.append("NUMERIC");
                break;
            case "timestamp":
                fieldQuery.append("DATETIME");
                break;
        }

        // Default DtTimestamp
        if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
            fieldQuery.append(" DEFAULT GETDATE()");
        }

        // Nullable
        if (field.getNullable().trim().equalsIgnoreCase("n")) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<MssqlDatabase> appliesTo() {
        return MssqlDatabase.class;
    }

}
