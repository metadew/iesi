package io.metadew.iesi.connection.operation;

import io.metadew.iesi.connection.database.TeradataDatabase;
import io.metadew.iesi.connection.database.connection.TeradataDatabaseConnection;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.configuration.action.trace.ActionTraceConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DbTeradataConnectionService {
    private static final Logger LOGGER = LogManager.getLogger();
    private static DbTeradataConnectionService INSTANCE;

    public synchronized static DbTeradataConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbTeradataConnectionService();
        }
        return INSTANCE;
    }

    private DbTeradataConnectionService() {
    }

    public TeradataDatabase getDatabase(Connection connection)  {
        String hostName = null;
        String databaseName= null;
        String userName = null;
        String userPassword = null;

        for (ConnectionParameter connectionParameter : connection.getParameters()) {
            if (connectionParameter.getName().equalsIgnoreCase("host")) {
                hostName = FrameworkCrypto.getInstance().decrypt(
                        FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()));
            } else if (connectionParameter.getName().equalsIgnoreCase("database")) {
                databaseName = FrameworkCrypto.getInstance().decrypt(
                        FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()));
            } else if (connectionParameter.getName().equalsIgnoreCase("user")) {
                userName = FrameworkCrypto.getInstance().decrypt(
                        FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()));
            } else if (connectionParameter.getName().equalsIgnoreCase("password")) {
                userPassword = FrameworkCrypto.getInstance().decrypt(
                        FrameworkControl.getInstance().resolveConfiguration(connectionParameter.getValue()));
            }
        }
        if (hostName == null || databaseName == null || userName == null || userPassword == null) {
            throw new RuntimeException();
        } else  {
            TeradataDatabaseConnection teradataDatabaseConnection = new TeradataDatabaseConnection(hostName, 0, databaseName, userName, userPassword);
            return new TeradataDatabase(teradataDatabaseConnection);
        }
    }

}
