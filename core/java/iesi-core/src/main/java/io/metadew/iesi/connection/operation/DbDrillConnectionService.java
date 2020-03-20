package io.metadew.iesi.connection.operation;

import io.metadew.iesi.connection.database.DrillDatabase;
import io.metadew.iesi.connection.database.connection.drill.DrillDatabaseConnection;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.definition.connection.Connection;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

@Log4j2
public class DbDrillConnectionService {

    private static final Logger LOGGER = LogManager.getLogger();
    private static DbDrillConnectionService INSTANCE;

    private final static String connectionModeKey = "mode";
    private final static String clusterNamesKey = "cluster";
    private final static String directoryNameKey = "directory";
    private final static String clusterIdKey = "clusterId";
    private final static String schemaKey = "schema";
    private final static String triesParameterKey = "tries";
    private final static String userKey = "user";
    private final static String passwordKey = "password";

    public synchronized static DbDrillConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbDrillConnectionService();
        }
        return INSTANCE;
    }

    private DbDrillConnectionService() {
    }

    public DrillDatabase getDatabase(Connection connection)  {

        String connectionMode = getMandatoryParameterWithKey(connection, connectionModeKey);
        String clusterName = getMandatoryParameterWithKey(connection, clusterNamesKey);
        String directoryName = getMandatoryParameterWithKey(connection, directoryNameKey);
        String clusterId = getMandatoryParameterWithKey(connection, clusterIdKey);
        String schemaName = getMandatoryParameterWithKey(connection, schemaKey);
        String triesParameter = getMandatoryParameterWithKey(connection,triesParameterKey);
        String userName = getMandatoryParameterWithKey(connection, userKey);
        String userPassword = getMandatoryParameterWithKey(connection, passwordKey);

        DrillDatabaseConnection drillDatabaseConnection = new DrillDatabaseConnection(connectionMode,
                clusterName,
                directoryName,
                clusterId,
                schemaName,
                triesParameter,
                userName,
                userPassword);
        return new DrillDatabase(drillDatabaseConnection, "");
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