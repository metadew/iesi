package io.metadew.iesi.connection.operation;
import io.metadew.iesi.connection.database.H2Database;
import io.metadew.iesi.connection.database.connection.h2.H2DatabaseConnection;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.definition.connection.Connection;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
@Log4j2
public class DbH2ConnectionService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static DbH2ConnectionService INSTANCE;

    private final static String connectionUrlKey = "connectionURL";
    private final static String schemaKey = "schema";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static DbH2ConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbH2ConnectionService();
        }
        return INSTANCE;
    }

    private DbH2ConnectionService() {
    }

    public H2Database getDatabase(Connection connection)  {

        String connectionUrl = getMandatoryParameterWithKey(connection, connectionUrlKey);
        String userName = getMandatoryParameterWithKey(connection, userKey);
        String schemaName = getMandatoryParameterWithKey(connection, schemaKey);
        String userPassword = getMandatoryParameterWithKey(connection, passwordKey);

        H2DatabaseConnection h2DatabaseConnection = new H2DatabaseConnection(connectionUrl,
                userName,
                userPassword);
        return new H2Database(h2DatabaseConnection, schemaName);
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
