package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.db2.Db2DatabaseConnection;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class Db2DatabaseServiceImpl extends SchemaDatabaseServiceImpl<Db2Database> implements SchemaDatabaseService<Db2Database> {

    private static Db2DatabaseServiceImpl INSTANCE;

    private final static String keyword = "db.db2";

    private final static String connectionUrlKey = "connectionURL";

    private final static String schemaKey = "schema";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String databaseKey = "database";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static Db2DatabaseServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Db2DatabaseServiceImpl();
        }
        return INSTANCE;
    }

    private Db2DatabaseServiceImpl() {}

    @Override
    public Db2Database getDatabase(Connection connection) {
        String userName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, schemaKey);
        Db2DatabaseConnection db2DatabaseConnection;
        if (DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            db2DatabaseConnection = new Db2DatabaseConnection(
                    DatabaseHandlerImpl.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    schemaName);
            return new Db2Database(db2DatabaseConnection, schemaName);
        }

        String hostName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection,portKey));
        String databaseName = DatabaseHandlerImpl.getInstance().getMandatoryParameterWithKey(connection, databaseKey);

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
                fieldQuery.append("DATE");
                break;
        }

        // Default DtTimestamp
        if (field.getDefaultTimestamp().trim().equalsIgnoreCase("y")) {
            fieldQuery.append(" DEFAULT CURRENT TIMESTAMP");
        }

        // Nullable
        if (field.getNullable().trim().equalsIgnoreCase("n")) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<Db2Database> appliesTo() {
        return Db2Database.class;
    }

}
