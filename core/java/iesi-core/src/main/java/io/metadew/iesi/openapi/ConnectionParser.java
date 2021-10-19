package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.log4j.Log4j2;

import java.net.MalformedURLException;
import java.net.URL;
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
        List<URL> addresses = getAddresses(openAPI.getServers());
        SecurityGroupKey securityGroupKey = SecurityGroupService.getInstance().get("PUBLIC")
                .map(Metadata::getMetadataKey)
                .orElseThrow(() -> new RuntimeException("could not find Security Group " + "PUBLIC"));
        return IntStream.range(0, addresses.size()).boxed()
                .map(index -> {
                    URL address = addresses.get(index);
                    String environment = String.format("env%s", index);
                    final List<ConnectionParameter> connectionParameters = new ArrayList<>();
                    getPort(address)
                            .ifPresent(port -> connectionParameters.add(
                                    new ConnectionParameter(name, environment, "port", port)
                            ));
                    getBaseUrl(address)
                            .ifPresent(baseUrl -> connectionParameters.add(
                                    new ConnectionParameter(name, environment, "baseUrl", baseUrl)
                            ));
                    connectionParameters.add(new ConnectionParameter(name, environment, "host", getHost(address)));
                    connectionParameters.add(new ConnectionParameter(name, environment, "tls", getProtocol(address)));
                    return new Connection(
                            name,
                            securityGroupKey,
                            "PUBLIC",
                            "http",
                            description,
                            environment,
                            connectionParameters);

                }).collect(Collectors.toList());
    }

    public List<URL> getAddresses(List<Server> servers) {
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
