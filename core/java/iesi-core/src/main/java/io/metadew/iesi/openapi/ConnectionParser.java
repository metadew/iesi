package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public class ConnectionParser {
    private static ConnectionParser INSTANCE;

    private ConnectionParser() {
    }

    public synchronized static ConnectionParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionParser();
        }
        return INSTANCE;
    }

    public List<Connection> parse(OpenAPI openAPI) {
        String name = openAPI.getInfo().getTitle();
        String description = openAPI.getInfo().getDescription();
        List<URL> adresses = getAdresses(openAPI.getServers());
        return IntStream.range(0, adresses.size()).boxed()
                .map(index -> {
                    URL address = adresses.get(index);
                    String environment = String.format("env%s", index);
                    final List<ConnectionParameter> connectionParameters = new ArrayList<>();
                    getPort(address)
                            .ifPresent(port -> connectionParameters.add(
                                    new ConnectionParameter(name, environment, "port", port)
                            ));
                    connectionParameters.add(new ConnectionParameter(name, environment, "host", getHost(address)));
                    connectionParameters.add(new ConnectionParameter(name, environment, "tls", getProtocol(address)));
                    connectionParameters.add(new ConnectionParameter(name, environment, "baseUrl", getBaseUrl(address)));


                    return new Connection(name, "http", description, environment, connectionParameters);

                }).collect(Collectors.toList());
    }

    public List<URL> getAdresses(List<Server> servers) {
        return servers.stream()
                .map(server -> toUrlModel(server.getUrl()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public String getHost(URL url) {
        return url.getHost();
    }


    public Optional<String> getPort(URL url) {
        int port = url.getPort();
        if (port == -1) return Optional.empty();
        return Optional.of(String.valueOf(port));
    }

    public String getProtocol(URL url) {
        String protocol = url.getProtocol();
        return protocol.equals("http") ? "N" : "Y";
    }

    public String getBaseUrl(URL url) {
        // TODO: base url is optional
        return url.getPath().substring(1, url.getPath().length() - 1);
    }

    public URL toUrlModel(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            log.warn(String.format("The url %s is malformed, it will be ignored", url));
            return null;
        }
    }
}
