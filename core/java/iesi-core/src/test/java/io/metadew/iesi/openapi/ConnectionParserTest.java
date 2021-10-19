package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class ConnectionParserTest {

    @Test
    public void parse() throws MalformedURLException {
        Info info = new Info()
                .version("1")
                .title("Documentation")
                .description("Documentation description");

        OpenAPI openAPI = new OpenAPI()
                .info(info)
                .components(new Components()
                        .addSecuritySchemes("petstore_auth", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2))
                        .addSecuritySchemes("api_key", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)))
                .addServersItem(new Server()
                        .url("https://petstore3.swagger.io/api/v3/"));

        URL address = new URL("https://petstore3.swagger.io/api/v3/");
        List<Connection> connections = new ArrayList<>();

        //Connection
        ConnectionParameter host = new ConnectionParameter(
                "Documentation",
                "env0",
                "host",
                ConnectionParser.getInstance().getHost(address));
        ConnectionParameter tls = new ConnectionParameter(
                "Documentation",
                "env0",
                "tls",
                ConnectionParser.getInstance().getProtocol(address));
        ConnectionParameter baseUrl = new ConnectionParameter(
                "Documentation",
                "env0",
                "baseUrl",
                ConnectionParser.getInstance().getBaseUrl(address).orElse(""));
        List<ConnectionParameter> connectionParameters = Arrays.asList(baseUrl, host,tls);
        Connection connection = new Connection(
                "Documentation",
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "http",
                "Documentation description",
                "env0",
                connectionParameters);
        connections.add(connection);

        //TEST
        assertThat(ConnectionParser.getInstance().parse(openAPI))
                .isEqualTo(connections);
    }

    @Test
    public void toUrlModel() throws MalformedURLException {
        assertThat(ConnectionParser.getInstance().toUrlModel("https://petstore3.swagger.io/api/v3/"))
                .isInstanceOf(URL.class)
                .isEqualTo(new URL("https://petstore3.swagger.io/api/v3/"));
    }

    @Test
    public void toUrlModelWrong() {
        assertThat(ConnectionParser.getInstance().toUrlModel("https//petstore3.swagger/api/v3/"))
                .isNull();
    }

    @Test
    public void getAddresses() throws MalformedURLException {
        List<Server> servers = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        Server server = new Server();
        server.setUrl("https://petstore3.swagger.io/api/v3/");
        URL url = new URL(server.getUrl());

        servers.add(server);
        urls.add(url);

        assertThat(ConnectionParser.getInstance().getAddresses(servers))
                .isEqualTo(urls);
    }

    @Test
    public void getAddressesWithWrongUrl() {
        List<Server> servers = new ArrayList<>();
        Server server = new Server();
        server.setUrl("https//petstore3.swagger/api/v3/");

        servers.add(server);

        assertThat(ConnectionParser.getInstance().getAddresses(servers))
                .isEqualTo(new ArrayList<>());
    }

    @Test
    public void getHost() throws MalformedURLException {
        assertThat(ConnectionParser.getInstance().getHost(new URL("https://petstore3.swagger.io/api/v3/")))
                .isEqualTo("petstore3.swagger.io");
    }
    @Test
    public void getHostWithoutBaseUrl() throws MalformedURLException {
        assertThat(ConnectionParser.getInstance().getHost(new URL("https://petstore3.swagger.io")))
                .isEqualTo("petstore3.swagger.io");
    }


    @Test
    public void getPortWithPortUndefined() throws MalformedURLException {
        assertThat(ConnectionParser.getInstance().getPort(new URL("https://petstore3.swagger.io/api/v3/")))
                .isEmpty();
    }

    @Test
    public void getPortWithPortDefined() throws MalformedURLException {
        assertThat(ConnectionParser.getInstance().getPort(new URL("http://petstore3.swagger.io:5000/api/v3/")))
                .isEqualTo(Optional.of("5000"));
    }

    @Test
    public void getProtocolWithTls() throws MalformedURLException {
        assertThat(ConnectionParser.getInstance().getProtocol(new URL("https://petstore3.swagger.io/api/v3/")))
                .isEqualTo("Y");
    }

    @Test
    public void getProtocolWithNoTls() throws MalformedURLException {
        assertThat(ConnectionParser.getInstance().getProtocol(new URL("http://petstore3.swagger.io/api/v3/")))
                .isEqualTo("N");
    }

    @Test
    public void getBaseUrl() throws MalformedURLException {
        assertThat(ConnectionParser.getInstance().getBaseUrl(new URL("http://petstore3.swagger.io/api/v3/")))
                .isEqualTo(Optional.of("/api/v3/"));
    }


    @Test
    public void getNoBaseUrl() throws MalformedURLException {
        assertThat(ConnectionParser.getInstance().getBaseUrl(new URL("http://petstore3.swagger.io")))
                .isEmpty();
    }

}
