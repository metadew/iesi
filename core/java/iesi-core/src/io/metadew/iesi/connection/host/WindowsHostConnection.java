package io.metadew.iesi.connection.host;

import io.metadew.iesi.connection.HostConnection;

/**
 * Connection object for a Windows host. This class extends the default host connection object.
 *
 * @author peter.billen
 */
public class WindowsHostConnection extends HostConnection {

    private static String type = "windows";

    public WindowsHostConnection(String hostName, String tempPath) {
        super(type, hostName, 0, "", "", tempPath, "", "", "Y");
    }

}
