package io.metadew.iesi.connection.database.mssql;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class MssqlDatabaseService extends SchemaDatabaseService<MssqlDatabase> implements ISchemaDatabaseService<MssqlDatabase> {

    private static MssqlDatabaseService INSTANCE;

    private final static String keyword = "db.mssql";

    private final static String connectionUrlKey = "connectionURL";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String databaseKey = "database";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);

    public synchronized static MssqlDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MssqlDatabaseService();
        }
        return INSTANCE;
    }

    private MssqlDatabaseService() {
    }


    @Override
    public MssqlDatabase getDatabase(Connection connection) {
        String userName = databaseHandler.getMandatoryParameterWithKey(connection, userKey);
        String userPassword = databaseHandler.getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = databaseHandler.getMandatoryParameterWithKey(connection, schemaKey);
        MssqlDatabaseConnection mssqlDatabaseConnection;
        if (databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            mssqlDatabaseConnection = new MssqlDatabaseConnection(
                    databaseHandler.getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    null,
                    schemaName);
            return new MssqlDatabase(mssqlDatabaseConnection, schemaName);
        }

        String hostName = databaseHandler.getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(databaseHandler.getMandatoryParameterWithKey(connection, portKey));
        String databaseName = databaseHandler.getMandatoryParameterWithKey(connection, databaseKey);

        mssqlDatabaseConnection = new MssqlDatabaseConnection(hostName,
                port,
                databaseName,
                userName,
                userPassword,
                null,
                schemaName);
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
            case STRING:
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(")");
                break;
            case CLOB:
                fieldQuery.append("VARCHAR (max)");
                break;
            case FLAG:
                fieldQuery.append("CHAR (").append(field.getLength()).append(")");
                break;
            case NUMBER:
                fieldQuery.append("NUMERIC");
                break;
            case TIMESTAMP:
                fieldQuery.append("DATETIME");
                break;
        }

        // Default DtTimestamp
        if (field.isDefaultTimestamp()) {
            fieldQuery.append(" DEFAULT GETDATE()");
        }

        // Nullable
        if (!field.isNullable()) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<MssqlDatabase> appliesTo() {
        return MssqlDatabase.class;
    }

}
