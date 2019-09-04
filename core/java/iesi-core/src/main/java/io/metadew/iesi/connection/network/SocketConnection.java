package io.metadew.iesi.connection.network;

import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.text.MessageFormat;

public class SocketConnection {

    private final String hostName;
    private final int port;

    public SocketConnection(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }

    public static SocketConnection from(Connection connection) {
        if (!connection.getType().equalsIgnoreCase("socket")) {
            // TODO
            throw new RuntimeException(MessageFormat.format("Cannot create Proxy connection from Connection of type {0}", connection.getType()));
        } else if (connection.getParameters().stream().anyMatch(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("hostname")) &&
                connection.getParameters().stream().anyMatch(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("port"))) {
            return new SocketConnection(
                    connection.getParameters().stream().filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("hostname")).findFirst().get().getValue(),
                    Integer.parseInt(connection.getParameters().stream().filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("port")).findFirst().get().getValue()));
        } else {
            throw new RuntimeException("Cannot create socket connection from Connection of with no definition of hostname and/or port");
        }
    }
}
