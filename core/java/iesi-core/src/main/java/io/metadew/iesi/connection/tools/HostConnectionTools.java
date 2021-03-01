package io.metadew.iesi.connection.tools;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import org.apache.commons.lang3.SystemUtils;

public final class HostConnectionTools {

    public static boolean isOnLocalhost(String connectionName, String environmentName) {
        if (connectionName.isEmpty()) {
            return true;
        } else if (connectionName.equalsIgnoreCase("localhost")) {
            return true;
        } else {
            Connection connection = ConnectionConfiguration.getInstance()
                    .get(new ConnectionKey(connectionName, environmentName))
                    .orElseThrow(() -> new RuntimeException(String.format("Unable to find connection %s", new ConnectionKey(connectionName, environmentName))));
            HostConnection hostConnection = ConnectionOperation.getInstance().getHostConnection(connection);

            return hostConnection.isOnLocalhost();
        }
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