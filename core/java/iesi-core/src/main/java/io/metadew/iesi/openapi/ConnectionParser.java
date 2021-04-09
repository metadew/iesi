package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.log4j.Log4j2;
import org.sqlite.SQLiteException;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public class ConnectionParser implements Parser<Connection> {
    private static ConnectionParser instance;

    private ConnectionParser() {
    }

    public static synchronized ConnectionParser getInstance() {
        if (instance == null) {
            instance = new ConnectionParser();
        }
        return instance;
    }

    public List<Connection> parse(OpenAPI openAPI) {
        String name = openAPI.getInfo().getTitle();
        String description = openAPI.getInfo().getDescription();
        Optional<Environment> environment = Optional.empty();
        List<URL> addresses = getAdresses(openAPI.getServers());
        try {
            environment = EnvironmentConfiguration.getInstance().getAll().stream().findFirst();
        } catch(RuntimeException exception) {

        }

        Optional<Environment> finalEnvironment = environment;
        return IntStream.range(0, addresses.size()).boxed()
                .map(index -> {
                    URL address = addresses.get(index);
                    final List<ConnectionParameter> connectionParameters = new ArrayList<>();
                    getPort(address)
                            .ifPresent(port -> connectionParameters.add(
                                    new ConnectionParameter(name, finalEnvironment.map(Environment::getName).orElse(null), "port", port)
                            ));
                    getBaseUrl(address)
                            .ifPresent(baseUrl -> connectionParameters.add(
                                    new ConnectionParameter(name,finalEnvironment.map(Environment::getName).orElse(null), "baseUrl", baseUrl)
                            ));
                    connectionParameters.add(new ConnectionParameter(name, finalEnvironment.map(Environment::getName).orElse(null), "host", getHost(address)));
                    connectionParameters.add(new ConnectionParameter(name, finalEnvironment.map(Environment::getName).orElse(null), "tls", getProtocol(address)));
                    return new Connection(name, "http", description, finalEnvironment.map(Environment::getName).orElse(null), connectionParameters);

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


    public Optional<String> getBaseUrl(URL url) {
        String baseUrl = url.getFile();
        if (baseUrl.equals("")) return Optional.empty();
        return Optional.of(baseUrl);
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
