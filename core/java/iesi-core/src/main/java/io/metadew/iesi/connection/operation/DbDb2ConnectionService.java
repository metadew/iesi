//package io.metadew.iesi.connection.operation;
//
//import io.metadew.iesi.connection.database.Db2Database;
//import io.metadew.iesi.connection.database.connection.db2.Db2DatabaseConnection;
//import io.metadew.iesi.framework.crypto.FrameworkCrypto;
//import io.metadew.iesi.framework.execution.FrameworkControl;
//import io.metadew.iesi.metadata.definition.connection.Connection;
//import lombok.extern.log4j.Log4j2;
//
//import java.text.MessageFormat;
//import java.util.Optional;
//
//@Log4j2
//public class DbDb2ConnectionService {
//    private static DbDb2ConnectionService INSTANCE;
//
//    private final static String connectionUrlKey = "connectionURL";
//
//    private final static String schemaKey = "schema";
//    private final static String hostKey = "host";
//    private final static String portKey = "port";
//    private final static String databaseKey = "database";
//    private final static String userKey = "user";
//    private final static String passwordKey = "password";
//
//    public synchronized static DbDb2ConnectionService getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new DbDb2ConnectionService();
//        }
//        return INSTANCE;
//    }
//
//    public Db2Database getDatabase(Connection connection)  {
//        String userName = getMandatoryParameterWithKey(connection, userKey);
//        String userPassword = getMandatoryParameterWithKey(connection, passwordKey);
//        String schemaName = getMandatoryParameterWithKey(connection, schemaKey);
//        Db2DatabaseConnection db2DatabaseConnection;
//        if (getOptionalParameterWithKey(connection, connectionUrlKey).isPresent()) {
//            db2DatabaseConnection = new Db2DatabaseConnection(
//                    getOptionalParameterWithKey(connection, connectionUrlKey).get(),
//                    userName,
//                    userPassword,
//                    schemaName);
//            return new Db2Database(db2DatabaseConnection, schemaName);
//        }
//
//        String hostName = getMandatoryParameterWithKey(connection, hostKey);
//        int port = Integer.parseInt(getMandatoryParameterWithKey(connection,portKey));
//        String databaseName = getMandatoryParameterWithKey(connection, databaseKey);
//
//        db2DatabaseConnection = new Db2DatabaseConnection(hostName,
//                port,
//                databaseName,
//                userName,
//                userPassword);
//        return new Db2Database(db2DatabaseConnection, "");
//    }
//    private String getMandatoryParameterWithKey(Connection connection, String key) {
//        return connection.getParameters().stream()
//                .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase(key))
//                .findFirst()
//                .map(connectionParameter -> FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()))
//                .map(connectionParameterValue -> FrameworkCrypto.getInstance().decryptIfNeeded(connectionParameterValue))
//                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Connection {0} does not contain mandatory parameter ''{1}''", connection, key)));
//
//    }
//    private Optional<String> getOptionalParameterWithKey(Connection connection, String key) {
//        return connection.getParameters().stream()
//                .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase(key))
//                .findFirst()
//                .map(connectionParameter -> FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()))
//                .map(connectionParameterValue -> FrameworkCrypto.getInstance().decryptIfNeeded(connectionParameterValue));
//    }
//}