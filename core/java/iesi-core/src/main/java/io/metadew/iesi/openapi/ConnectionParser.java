package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionParser {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ConnectionParser INSTANCE;
    public static String[] fakeEnvs = new String[] {"TST", "PRD", "ENVV"};


    private ConnectionParser() {}

    public synchronized static ConnectionParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionParser();
        }
        return INSTANCE;
    }

    public  List<Connection> parse(OpenAPI openAPI) {
        String name = openAPI.getInfo().getTitle();
        String description = openAPI.getInfo().getDescription();
        List<URL> adresses = getAdresses(openAPI.getServers());
        return adresses.stream().map(address -> {
            int index = adresses.indexOf(address);
            String environment = String.format("env%s", index);
            List<ConnectionParameter> connectionParameters;
            ConnectionParameter port = new ConnectionParameter(name, environment, "port", getPort(address));
            ConnectionParameter host = new ConnectionParameter(name, environment, "host", getHost(address));
            ConnectionParameter tls = new ConnectionParameter(name, environment, "tls", getProtocol(address));
            ConnectionParameter baseUrl = new ConnectionParameter(name, environment, "baseUrl", getBaseUrl(address));

            if (getPort(address) == null) {
                connectionParameters = Arrays.asList(host, tls, baseUrl);
            } else {
                connectionParameters = Arrays.asList(host, port, tls, baseUrl);
            }

            return new Connection(name, "http", description, environment,connectionParameters);

        }).collect(Collectors.toList());
    }

    public List<URL> getAdresses(List<Server> servers) {
        return servers.stream().map(server -> toUrlModel(server.getUrl())).filter(url -> url != null).collect(Collectors.toList());
    }

    public String getHost(URL url) {
        return url.getHost();
    }


    public String getPort(URL url)  {
        int port = url.getPort();
        if (port == -1 ) return null;
        return String.valueOf(port);
    }

    public String getProtocol(URL url) {
        String protocol = url.getProtocol();
        return protocol.equals("http") ? "N" : "Y";
    }

    public String getBaseUrl(URL url) {
        return url.getPath().substring(1, url.getPath().length() - 1);
    }

    public URL toUrlModel(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            LOGGER.fatal(String.format("The url %s is malformed please follow the standard annotation", url));
            return null;
        }
    }
}
