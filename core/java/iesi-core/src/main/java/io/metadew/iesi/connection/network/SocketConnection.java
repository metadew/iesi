package io.metadew.iesi.connection.network;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;

import java.text.MessageFormat;

public class SocketConnection {

    private final String hostName;
    private final int port;
    private final String encoding;

    public SocketConnection(String hostName, int port, String encoding) {
        this.hostName = hostName;
        this.port = port;
        this.encoding = encoding;
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
                    connection.getParameters().stream()
                            .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("hostname"))
                            .findFirst()
                            .map(ConnectionParameter::getValue)
                            .orElseThrow(() -> new RuntimeException("Cannot create socket connection from Connection of with no definition of hostname and/or port")),
                    Integer.parseInt(connection.getParameters().stream()
                            .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("port"))
                            .findFirst()
                            .map(ConnectionParameter::getValue)
                            .orElseThrow(() -> new RuntimeException("Cannot create socket connection from Connection of with no definition of hostname and/or port"))),
                    connection.getParameters().stream()
                            .filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("encoding"))
                            .findFirst()
                            .map(ConnectionParameter::getValue)
                            .orElse("UTF-16"));
        } else {
            throw new RuntimeException("Cannot create socket connection from Connection of with no definition of hostname and/or port");
        }
    }

    public String getEncoding() {
        return encoding;
    }
}
