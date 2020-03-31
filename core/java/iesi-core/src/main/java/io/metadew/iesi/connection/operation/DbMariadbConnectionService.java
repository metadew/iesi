//package io.metadew.iesi.connection.operation;
//
//import io.metadew.iesi.connection.database.MariadbDatabase;
//import io.metadew.iesi.connection.database.connection.mariadb.MariadbDatabaseConnection;
//import io.metadew.iesi.framework.crypto.FrameworkCrypto;
//import io.metadew.iesi.framework.execution.FrameworkControl;
//import io.metadew.iesi.metadata.definition.connection.Connection;
//import lombok.extern.log4j.Log4j2;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.text.MessageFormat;
//import java.util.Optional;
//
//@Log4j2
//public class DbMariadbConnectionService {
//    private static final Logger LOGGER = LogManager.getLogger();
//    private static DbMariadbConnectionService INSTANCE;
//
//    private final static String connectionUrlKey = "connectionURL";
//    private final static String hostKey = "host";
//    private final static String portKey = "port";
//    private final static String databaseKey = "database";
//    private final static String userKey = "user";
//    private final static String passwordKey = "password";
//
//    public synchronized static DbMariadbConnectionService getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new DbMariadbConnectionService();
//        }
//        return INSTANCE;
//    }
//
//    public MariadbDatabase getDatabase(Connection connection) {
//        String userName = getMandatoryParameterWithKey(connection, userKey);
//        String userPassword = getMandatoryParameterWithKey(connection, passwordKey);
//        MariadbDatabaseConnection mariadbDatabaseConnection;
//        if (getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
//            mariadbDatabaseConnection = new MariadbDatabaseConnection(
//                    getOptionalParameterWithKey(connection, connectionUrlKey).get(),
//                    userName,
//                    userPassword);
//            return new MariadbDatabase(mariadbDatabaseConnection);
//        }
//
//        String hostName = getMandatoryParameterWithKey(connection, hostKey);
//        int port = Integer.parseInt(getMandatoryParameterWithKey(connection, portKey));
//        String databaseName = getMandatoryParameterWithKey(connection, databaseKey);
//
//        mariadbDatabaseConnection = new MariadbDatabaseConnection(hostName,
//                port,
//                databaseName,
//                userName,
//                userPassword);
//        return new MariadbDatabase(mariadbDatabaseConnection);
//    }
//
//    private String getMandatoryParameterWithKey(Connection connection, String key) {
//        return connection.getParameters().stream()
//                .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase(key))
//                .findFirst()
//                .map(connectionParameter -> FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()))
//                .map(connectionParameterValue -> FrameworkCrypto.getInstance().decryptIfNeeded(connectionParameterValue))
//                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Connection {0} does not contain mandatory parameter ''{1}''", connection, key)));
//
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