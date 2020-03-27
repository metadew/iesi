//package io.metadew.iesi.connection.operation;
//
//import io.metadew.iesi.connection.database.H2Database;
//import io.metadew.iesi.connection.database.connection.h2.H2DatabaseConnection;
//import io.metadew.iesi.connection.database.connection.h2.H2EmbeddedDatabaseConnection;
//import io.metadew.iesi.connection.database.connection.h2.H2MemoryDatabaseConnection;
//import io.metadew.iesi.connection.database.connection.h2.H2ServerDatabaseConnection;
//import io.metadew.iesi.framework.crypto.FrameworkCrypto;
//import io.metadew.iesi.framework.execution.FrameworkControl;
//import io.metadew.iesi.metadata.definition.connection.Connection;
//import lombok.extern.log4j.Log4j2;
//
//import java.text.MessageFormat;
//import java.util.Optional;
//
//@Log4j2
//public class DbH2ConnectionService {
//
//    private static DbH2ConnectionService INSTANCE;
//
//    private final static String connectionUrlKey = "connectionURL";
//
//    private final static String modeKey = "mode";
//    private final static String embeddedModeKey = "embedded";
//    private final static String serverModeKey = "server";
//    private final static String memoryModeKey = "memory";
//    private final static String fileKey = "file";
//    private final static String databaseKey = "database";
//    private final static String hostKey = "host";
//    private final static String portKey = "port";
//    private final static String schemaKey = "schema";
//    private final static String userKey = "user";
//    private final static String passwordKey = "password";
//
//    public synchronized static DbH2ConnectionService getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new DbH2ConnectionService();
//        }
//        return INSTANCE;
//    }
//
//    private DbH2ConnectionService() {
//    }
//
//    public H2Database getDatabase(Connection connection) {
//        String userName = getMandatoryParameterWithKey(connection, userKey);
//        String userPassword = getMandatoryParameterWithKey(connection, passwordKey);
//        String schemaName = getMandatoryParameterWithKey(connection, schemaKey);
//        H2DatabaseConnection h2DatabaseConnection;
//        if (getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
//            h2DatabaseConnection = new H2DatabaseConnection(
//                    getOptionalParameterWithKey(connection, connectionUrlKey).get(),
//                    userName,
//                    userPassword,
//                    schemaName);
//            return new H2Database(h2DatabaseConnection, schemaName);
//        }
//        String mode = getMandatoryParameterWithKey(connection, modeKey);
//        switch (mode) {
//            case embeddedModeKey:
//                h2DatabaseConnection = new H2EmbeddedDatabaseConnection(
//                        getMandatoryParameterWithKey(connection, fileKey),
//                        userName,
//                        userPassword,
//                        schemaName);
//                return new H2Database(h2DatabaseConnection, schemaName);
//            case serverModeKey:
//                String host = getMandatoryParameterWithKey(connection, hostKey);
//                int port = Integer.parseInt(getMandatoryParameterWithKey(connection, portKey));
//                h2DatabaseConnection = new H2ServerDatabaseConnection(
//                        host,
//                        port,
//                        getMandatoryParameterWithKey(connection, fileKey),
//                        userName,
//                        userPassword,
//                        schemaName);
//                return new H2Database(h2DatabaseConnection, schemaName);
//            case memoryModeKey:
//                h2DatabaseConnection = new H2MemoryDatabaseConnection(
//                        getMandatoryParameterWithKey(connection, databaseKey),
//                        userName,
//                        userPassword,
//                        schemaName);
//                return new H2Database(h2DatabaseConnection, schemaName);
//            default:
//                throw new RuntimeException("H2 database " + connection + " does not know mode '" + mode + "'");
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
