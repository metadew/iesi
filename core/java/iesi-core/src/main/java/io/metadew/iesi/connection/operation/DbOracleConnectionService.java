//package io.metadew.iesi.connection.operation;
//
//import io.metadew.iesi.connection.database.OracleDatabase;
//import io.metadew.iesi.connection.database.connection.oracle.OracleDatabaseConnection;
//import io.metadew.iesi.connection.database.connection.oracle.ServiceNameOracleDatabaseConnection;
//import io.metadew.iesi.connection.database.connection.oracle.TnsAliasOracleDatabaseConnection;
//import io.metadew.iesi.framework.crypto.FrameworkCrypto;
//import io.metadew.iesi.framework.execution.FrameworkControl;
//import io.metadew.iesi.metadata.definition.connection.Connection;
//
//import java.text.MessageFormat;
//import java.util.Optional;
//
//public class DbOracleConnectionService {
//    private static DbOracleConnectionService INSTANCE;
//
//    private final static String connectionUrlKey = "connectionURL";
//    private final static String modeKey = "mode";
//    private final static String tnsAliasModeKey = "tnsAlias";
//    private final static String serviceModeKey = "service";
//    private final static String serviceKey = "service";
//    private final static String tnsAliasKey = "tnsAlias";
//    private final static String hostKey = "host";
//    private final static String portKey = "port";
//    private final static String schemaKey = "schema";
//    private final static String userKey = "user";
//    private final static String passwordKey = "password";
//
//    public synchronized static DbOracleConnectionService getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new DbOracleConnectionService();
//        }
//        return INSTANCE;
//    }
//
//    private DbOracleConnectionService() {
//    }
//
//    public OracleDatabase getDatabase(Connection connection) {
//        String userName = getMandatoryParameterWithKey(connection, userKey);
//        String userPassword = getMandatoryParameterWithKey(connection, passwordKey);
//        String schemaName = getMandatoryParameterWithKey(connection, schemaKey);
//        OracleDatabaseConnection oracleDatabaseConnection;
//        if (getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
//            oracleDatabaseConnection = new OracleDatabaseConnection(
//                    getOptionalParameterWithKey(connection, connectionUrlKey).get(),
//                    userName,
//                    userPassword,
//                    schemaName);
//            return new OracleDatabase(oracleDatabaseConnection, schemaName);
//        }
//        String mode = getMandatoryParameterWithKey(connection, modeKey);
//        String host = getMandatoryParameterWithKey(connection, hostKey);
//        int port = Integer.parseInt(getMandatoryParameterWithKey(connection, portKey));
//        switch (mode) {
//            case tnsAliasModeKey:
//                oracleDatabaseConnection = new TnsAliasOracleDatabaseConnection(
//                        host,
//                        port,
//                        getMandatoryParameterWithKey(connection, tnsAliasKey),
//                        userName,
//                        userPassword,
//                        schemaName);
//                return new OracleDatabase(oracleDatabaseConnection, schemaName);
//            case serviceModeKey:
//                oracleDatabaseConnection = new ServiceNameOracleDatabaseConnection(
//                        host,
//                        port,
//                        getMandatoryParameterWithKey(connection, serviceKey),
//                        userName,
//                        userPassword,
//                        schemaName);
//                return new OracleDatabase(oracleDatabaseConnection, schemaName);
//            default:
//                throw new RuntimeException("Oracle database " + connection + " does not know mode '" + mode + "'");
//        }
//
//    }
//
//    private String getMandatoryParameterWithKey(Connection connection, String key) {
//        return connection.getParameters().stream()
//                .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase(key))
//                .findFirst()
//                .map(connectionParameter -> FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()))
//                .map(connectionParameterValue -> FrameworkCrypto.getInstance().decryptIfNeeded(connectionParameterValue))
//                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Connection {0} does not contain mandatory parameter ''{1}''", connection, key)));
//    }
//
//    private Optional<String> getOptionalParameterWithKey(Connection connection, String key) {
//        return connection.getParameters().stream()
//                .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase(key))
//                .findFirst()
//                .map(connectionParameter -> FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()))
//                .map(connectionParameterValue -> FrameworkCrypto.getInstance().decryptIfNeeded(connectionParameterValue));
//    }
//}
