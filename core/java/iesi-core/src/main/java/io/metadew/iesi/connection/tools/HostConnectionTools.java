package io.metadew.iesi.connection.tools;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import org.apache.commons.lang3.SystemUtils;

public final class HostConnectionTools {

    public static boolean isOnLocalhost(String connectionName, String environmentName) throws Exception {
        boolean isOnLocalhost = true;

        if (connectionName.isEmpty()) {
            isOnLocalhost = true;
        } else if (connectionName.equalsIgnoreCase("localhost")) {
            isOnLocalhost = true;
        } else {
            Connection connection = ConnectionConfiguration.getInstance()
                    .get(new ConnectionKey(connectionName, environmentName))
                    .get();
            ConnectionOperation connectionOperation = new ConnectionOperation();
            HostConnection hostConnection = connectionOperation.getHostConnection(connection);

            if (hostConnection.isOnLocalhost()) {
                isOnLocalhost = true;
            } else {
                isOnLocalhost = false;
            }
        }

        return isOnLocalhost;
    }

    public static String getLocalhostType() {
        String result = "";
        if (SystemUtils.IS_OS_WINDOWS) {
            result = "windows";
        } else {
            result = "linux";
        }
        return result;
    }

}