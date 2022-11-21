package io.metadew.iesi.connection.database.oracle;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.ISchemaDatabaseService;
import io.metadew.iesi.connection.database.SchemaDatabaseService;
import io.metadew.iesi.metadata.definition.MetadataField;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.util.Optional;

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
        String userName = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, userKey);
        String userPassword = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, passwordKey);
        Optional<String> schemaName = SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, schemaKey);
        OracleDatabaseConnection oracleDatabaseConnection;
        if (SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
            oracleDatabaseConnection = new OracleDatabaseConnection(
                    SpringContext.getBean(DatabaseHandler.class).getOptionalParameterWithKey(connection, connectionUrlKey).get(),
                    userName,
                    userPassword,
                    null,
                    schemaName.orElse(null));
            return new OracleDatabase(oracleDatabaseConnection, schemaName.orElse(null));
        }
        String mode = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, modeKey);
        String host = SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, portKey));
        switch (mode) {
            case tnsAliasModeKey:
                oracleDatabaseConnection = new TnsAliasOracleDatabaseConnection(
                        host,
                        port,
                        SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, tnsAliasKey),
                        userName,
                        userPassword,
                        null,
                        schemaName.orElse(null));
                return new OracleDatabase(oracleDatabaseConnection, schemaName.orElse(null));
            case serviceModeKey:
                oracleDatabaseConnection = new ServiceNameOracleDatabaseConnection(
                        host,
                        port,
                        SpringContext.getBean(DatabaseHandler.class).getMandatoryParameterWithKey(connection, serviceKey),
                        userName,
                        userPassword,
                        null,
                        schemaName.orElse(null));
                return new OracleDatabase(oracleDatabaseConnection, schemaName.orElse(null));
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
            case STRING:
                fieldQuery.append("VARCHAR2 (").append(field.getLength()).append(" CHAR)");
                break;
            case FLAG:
                fieldQuery.append("CHAR (").append(field.getLength()).append(" CHAR)");
                break;
            case NUMBER:
                fieldQuery.append("NUMBER");
                break;
            case TIMESTAMP:
                fieldQuery.append("TIMESTAMP (6)");
                break;
            case CLOB:
                fieldQuery.append("CLOB");
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
