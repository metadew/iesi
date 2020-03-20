package io.metadew.iesi.connection.operation;
import io.metadew.iesi.connection.database.PrestoDatabase;
import io.metadew.iesi.connection.database.connection.presto.PrestoDatabaseConnection;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.definition.connection.Connection;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

@Log4j2
public class DbPrestoConnectionService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static DbPrestoConnectionService INSTANCE;

    private final static String hostKey = "host";
    private final static String catalogNameKey = "catalog";
    private final static String portKey = "port";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static DbPrestoConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbPrestoConnectionService();
        }
        return INSTANCE;
    }

    private DbPrestoConnectionService() {
    }
    public PrestoDatabase getDatabase(Connection connection)  {

        String hostName = getMandatoryParameterWithKey(connection, hostKey);
        int port = Integer.parseInt(getMandatoryParameterWithKey(connection,portKey));
        String catalogName = getMandatoryParameterWithKey(connection, catalogNameKey);
        String schemaName = getMandatoryParameterWithKey(connection, schemaKey);
        String userName = getMandatoryParameterWithKey(connection, userKey);
        String userPassword = getMandatoryParameterWithKey(connection, passwordKey);

        PrestoDatabaseConnection prestoDatabaseConnection = new PrestoDatabaseConnection(hostName,
                port,
                catalogName,
                schemaName,
                userName,
                userPassword);
        return new PrestoDatabase(prestoDatabaseConnection, schemaName);
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
