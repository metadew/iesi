package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionParser {
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
            String env = fakeEnvs[adresses.indexOf(address)];

            ConnectionParameter host = new ConnectionParameter(name, env, "host", getHost(address));
            ConnectionParameter port = new ConnectionParameter(name, env, "port", getPort(address));
            ConnectionParameter tls = new ConnectionParameter(name, env, "tls", getProtocol(address));
            ConnectionParameter baseUrl = new ConnectionParameter(name, env, "baseUrl", getBaseUrl(address));
            List<ConnectionParameter> connectionParameters = new ArrayList<>(Arrays.asList(host, port, tls, baseUrl));
            return new Connection(name, "http", description, env,connectionParameters);
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
            e.printStackTrace();
            return null;
        }
    }
}
