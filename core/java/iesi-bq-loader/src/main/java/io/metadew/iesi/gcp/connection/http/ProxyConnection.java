package io.metadew.iesi.gcp.connection.http;

public class ProxyConnection {

    private final String hostName;
    private final int port;

    public ProxyConnection(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }

}
