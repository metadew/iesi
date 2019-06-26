package io.metadew.iesi.connection.tools;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.operation.ConnectionOperation;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.Connection;
import org.apache.commons.lang3.SystemUtils;

public final class HostConnectionTools {

    public static boolean isOnLocalhost(FrameworkExecution frameworkExecution, String connectionName, String environmentName) {
        boolean isOnLocalhost = true;

        if (connectionName.isEmpty()) {
            isOnLocalhost = true;
        } else if (connectionName.equalsIgnoreCase("localhost")) {
            isOnLocalhost = true;
        } else {
            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(frameworkExecution.getFrameworkInstance());
            Connection connection = connectionConfiguration
                    .getConnection(connectionName, environmentName).get();
            ConnectionOperation connectionOperation = new ConnectionOperation(frameworkExecution);
            HostConnection hostConnection = connectionOperation.getHostConnection(connection);

            if (hostConnection.isOnLocalhost(frameworkExecution)) {
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