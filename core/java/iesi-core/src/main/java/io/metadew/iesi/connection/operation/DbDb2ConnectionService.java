package io.metadew.iesi.connection.operation;
import io.metadew.iesi.connection.database.Db2Database;
import io.metadew.iesi.connection.database.connection.db2.Db2DatabaseConnection;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.definition.connection.Connection;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

@Log4j2
public class DbDb2ConnectionService {
    private static final Logger LOGGER = LogManager.getLogger();
    private static DbDb2ConnectionService INSTANCE;

    private final static String hostKey = "host";
    private final static String portKey = "port";
    private final static String databaseKey = "database";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static DbDb2ConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbDb2ConnectionService();
        }
        return INSTANCE;
    }

    public Db2Database getDatabase(Connection connection)  {

        String hostName = getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(getMandatoryParameterWithKey(connection,portKey));
        String databaseName = getMandatoryParameterWithKey(connection, databaseKey);
        String userName = getMandatoryParameterWithKey(connection, userKey);
        String userPassword = getMandatoryParameterWithKey(connection, passwordKey);

        Db2DatabaseConnection db2DatabaseConnection = new Db2DatabaseConnection(hostName,
                port,
                databaseName,
                userName,
                userPassword);
        return new Db2Database(db2DatabaseConnection, "");
    }
    private String getMandatoryParameterWithKey(Connection connection, String key) {
        return connection.getParameters().stream()
                .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase(key))
                .findFirst()
                .map(connectionParameter -> FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()))
                .map(connectionParameterValue -> FrameworkCrypto.getInstance().decryptIfNeeded(connectionParameterValue))
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Connection {0} does not contain mandatory parameter ''{1}''", connection, key)));

    }
}