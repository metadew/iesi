package io.metadew.iesi.connection.host;

import io.metadew.iesi.connection.HostConnection;

/**
 * Connection object for a Linux host. This class extends the default host connection object.
 *
 * @author peter.billen
 */
public class LinuxHostConnection extends HostConnection {

    private static String type = "linux";

    public LinuxHostConnection(String hostName, int portNumber, String userName, String userPassword, String tempPath,
                               String terminalFlag, String jumphostConnectionName, String allowLocalhostExecution) {
        super(type, hostName, portNumber, userName, userPassword, tempPath, terminalFlag, jumphostConnectionName, allowLocalhostExecution);
    }

}
