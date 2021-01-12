package io.metadew.iesi.connection.http;

import io.metadew.iesi.metadata.definition.connection.Connection;
import lombok.Data;

import java.text.MessageFormat;

@Data
public class ProxyConnection {

    private final String hostName;
    private final int port;

    public ProxyConnection(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public static ProxyConnection from(Connection connection) {
        if (!connection.getType().equalsIgnoreCase("proxy")) {
            // TODO
            throw new RuntimeException(MessageFormat.format("Cannot create Proxy connection from Connection of type {0}", connection.getType()));
        } else if (connection.getParameters().stream().anyMatch(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("hostname")) &&
                connection.getParameters().stream().anyMatch(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("port"))) {
            return new ProxyConnection(
                    connection.getParameters().stream().filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("hostname")).findFirst().get().getValue(),
                    Integer.parseInt(connection.getParameters().stream().filter(connectionParameter -> connectionParameter.getName().equalsIgnoreCase("port")).findFirst().get().getValue()));
        } else {
            throw new RuntimeException("Cannot create Proxy connection from Connection of with no definition of hostname and/or port");
        }
    }
}
