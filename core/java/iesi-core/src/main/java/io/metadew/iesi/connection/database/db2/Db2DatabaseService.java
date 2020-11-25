package io.metadew.iesi.connection.database.db2;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class Db2DatabaseService extends SchemaDatabaseService<Db2Database> implements ISchemaDatabaseService<Db2Database> {

    private static Db2DatabaseService INSTANCE;

    private final static String keyword = "db.db2";

    private final static String connectionUrlKey = "connectionURL";

    private final static String schemaKey = "schema";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String databaseKey = "database";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static Db2DatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Db2DatabaseService();
        }
        return INSTANCE;
    }

    private Db2DatabaseService() {}

    @Override
    public Db2Database getDatabase(Connection connection) {
        String userName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, schemaKey);
        Db2DatabaseConnection db2DatabaseConnection;
        if (DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            db2DatabaseConnection = new Db2DatabaseConnection(
                    DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new Db2Database(db2DatabaseConnection, schemaName);
        }

        String hostName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection,portKey));
        String databaseName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, databaseKey);

        db2DatabaseConnection = new Db2DatabaseConnection(hostName,
                port,
                databaseName,
                userName,
                userPassword);
        return new Db2Database(db2DatabaseConnection, schemaName);
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(Db2Database db2Database) {
        return "CURRENT TIMESTAMP";
    }

    @Override
    public String getAllTablesQuery(Db2Database db2Database, String pattern) {
        return "";
    }

    @Override
    public String createQueryExtras(Db2Database db2Database) {
        return "";
    }

    @Override
    public boolean addComments(Db2Database db2Database) {
        return true;
    }

    @Override
    public String toQueryString(Db2Database db2Database, MetadataField field) {
        StringBuilder fieldQuery = new StringBuilder();
        // Data Types
        switch (field.getType()) {
            case STRING:
                fieldQuery.append("VARCHAR (").append(field.getLength()).append(")");
                break;
            case CLOB:
                fieldQuery.append("CLOB").append(field.getLength()).append(")");
                break;
            case FLAG:
                fieldQuery.append("CHAR (").append(field.getLength()).append(")");
                break;
            case NUMBER:
                fieldQuery.append("NUMERIC");
                break;
            case TIMESTAMP:
                fieldQuery.append("DATE");
                break;
        }

        // Default DtTimestamp
        if (field.isDefaultTimestamp()) {
            fieldQuery.append(" DEFAULT CURRENT TIMESTAMP");
        }

        // Nullable
        if (!field.isNullable()) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<Db2Database> appliesTo() {
        return Db2Database.class;
    }

}
