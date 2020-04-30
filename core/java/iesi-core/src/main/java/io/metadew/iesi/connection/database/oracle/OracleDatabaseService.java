package io.metadew.iesi.connection.database.oracle;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

public class OracleDatabaseService extends SchemaDatabaseService<OracleDatabase> implements ISchemaDatabaseService<OracleDatabase> {

    private static OracleDatabaseService INSTANCE;

    private final static String keyword = "db.oracle";

    private final static String connectionUrlKey = "connectionURL";
    private final static String modeKey = "mode";
    private final static String tnsAliasModeKey = "tnsAlias";
    private final static String serviceModeKey = "service";
    private final static String serviceKey = "service";
    private final static String tnsAliasKey = "tnsAlias";
    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static OracleDatabaseService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OracleDatabaseService();
        }
        return INSTANCE;
    }

    private OracleDatabaseService() {}

    @Override
    public OracleDatabase getDatabase(Connection connection) {
        String userName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, userKey);
        String userPassword = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, passwordKey);
        String schemaName = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, schemaKey);
        OracleDatabaseConnection oracleDatabaseConnection;
        if (DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            oracleDatabaseConnection = new OracleDatabaseConnection(
                    DatabaseHandler.getInstance().getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    null,
                    schemaName);
            return new OracleDatabase(oracleDatabaseConnection, schemaName);
        }
        String mode = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, modeKey);
        String host = DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, portKey));
        switch (mode) {
            case tnsAliasModeKey:
                oracleDatabaseConnection = new TnsAliasOracleDatabaseConnection(
                        host,
                        port,
                        DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, tnsAliasKey),
                        userName,
                        userPassword,
                        null,
                        schemaName);
                return new OracleDatabase(oracleDatabaseConnection, schemaName);
            case serviceModeKey:
                oracleDatabaseConnection = new ServiceNameOracleDatabaseConnection(
                        host,
                        port,
                        DatabaseHandler.getInstance().getMandatoryParameterWithKey(connection, serviceKey),
                        userName,
                        userPassword,
                        null,
                        schemaName);
                return new OracleDatabase(oracleDatabaseConnection, schemaName);
            default:
                throw new RuntimeException("Oracle database " + connection + " does not know mode '" + mode + "'");
        }
    }

    @Override
    public String keyword() {
        return keyword;
    }

    @Override
    public String getSystemTimestampExpression(OracleDatabase oracleDatabase) {
        return "systimestamp";
    }

    @Override
    public String getAllTablesQuery(OracleDatabase oracleDatabase, String pattern) {
        // pattern = tableNamePrefix + categoryPrefix
        return "select OWNER, TABLE_NAME from ALL_TABLES where"
                + oracleDatabase.getSchema().map(schema -> " owner = '" + schema + "' and").orElse("")
                + " TABLE_NAME like '"
                + pattern
                + "%' order by TABLE_NAME ASC";
    }

    public boolean addComments(OracleDatabase oracleDatabase) {
        return true;
    }

    public String createQueryExtras(OracleDatabase oracleDatabase) {
        return "\nLOGGING\nNOCOMPRESS\nNOCACHE\nNOPARALLEL\nMONITORING";
    }

    public String toQueryString(OracleDatabase oracleDatabase, MetadataField field) {
        StringBuilder fieldQuery = new StringBuilder();
        // Data Types
        switch (field.getType()) {
            case "string":
                fieldQuery.append("VARCHAR2 (").append(field.getLength()).append(" CHAR)");
                break;
            case "flag":
                fieldQuery.append("CHAR (").append(field.getLength()).append(" CHAR)");
                break;
            case "number":
                fieldQuery.append("NUMBER");
                break;
            case "timestamp":
                fieldQuery.append("TIMESTAMP (6)");
                break;
        }

        // Default DtTimestamp
        if (field.isDefaultTimestamp()) {
            fieldQuery.append(" DEFAULT systimestamp");
        }

        // Nullable
        if (!field.isNullable()) {
            fieldQuery.append(" NOT NULL");
        }
        return fieldQuery.toString();
    }

    @Override
    public Class<OracleDatabase> appliesTo() {
        return OracleDatabase.class;
    }

}
