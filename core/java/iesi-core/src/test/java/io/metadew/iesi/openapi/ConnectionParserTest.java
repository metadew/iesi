package io.metadew.iesi.openapi;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.key.MetadataKey;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.Test;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ConnectionParserTest {


    private ConnectionParser connectionParser = ConnectionParser.getInstance();

    @Test
    public void parse() throws MalformedURLException {
        OpenAPI openAPIMock = mock(OpenAPI.class);
        Info infoMock = mock(Info.class);
        Server serverMock = mock(Server.class);
        URL address = new URL("https://petstore3.swagger.io/api/v3/");
        List<Connection> connections = new ArrayList<>();
        List<Server> servers = new ArrayList<Server>(){{add(serverMock);}};
        List<ConnectionParameter> connectionParameters;

        //Open API Info
        when(infoMock.getTitle()).thenReturn("Documentation");
        when(infoMock.getDescription()).thenReturn("Documentation description");
        //Open API Servers
        when(serverMock.getUrl()).thenReturn("https://petstore3.swagger.io/api/v3/");
        //Open API
        when(openAPIMock.getInfo()).thenReturn(infoMock);
        when(openAPIMock.getServers()).thenReturn(servers);
        //Connection
        ConnectionParameter host = new ConnectionParameter(infoMock.getTitle(), "TST", "host", connectionParser.getHost(address));
        ConnectionParameter tls = new ConnectionParameter(infoMock.getTitle(), "TST", "tls", connectionParser.getProtocol(address));
        ConnectionParameter baseUrl = new ConnectionParameter(infoMock.getTitle(), "TST", "baseUrl", connectionParser.getBaseUrl(address));
        connectionParameters = Arrays.asList(host, tls, baseUrl);
        Connection connection = new Connection(infoMock.getTitle(), "http", infoMock.getDescription(), "TST",connectionParameters);
        connections.add(connection);

        //TEST
        assertThat(connectionParser.parse(openAPIMock)).isEqualTo(connections);


    }

    @Test
    public void toUrlModel() {
        assertThat(connectionParser.toUrlModel("https://petstore3.swagger.io/api/v3/")).isInstanceOf(URL.class);
    }

    @Test
    public void toUrlModelWrong(){
        assertThat(connectionParser.toUrlModel("https//petstore3.swagger/api/v3/")).isEqualTo(null);
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

        assertThat(connectionParser.getAdresses(servers)).isEqualTo(urls);

    }

    @Test
    public void getAddressesWithWrongUrl() {
        List<Server> servers = new ArrayList<>();
        Server server = new Server();
        server.setUrl("https//petstore3.swagger/api/v3/");

        servers.add(server);

        assertThat(connectionParser.getAdresses(servers)).isEqualTo(new ArrayList<>());

    }

    @Test
    public void getHost() throws MalformedURLException {

        assertThat(connectionParser.getHost(new URL("https://petstore3.swagger.io/api/v3/"))).isEqualTo("petstore3.swagger.io");
    }

    @Test
    public void getPortWithPotUndefined() throws MalformedURLException {

        assertThat(connectionParser.getPort(new URL("https://petstore3.swagger.io/api/v3/"))).isEqualTo(null);
    }

    @Test
    public void getPortWithPortDefined() throws MalformedURLException {

        assertThat(connectionParser.getPort(new URL("http://petstore3.swagger.io:5000/api/v3/"))).isEqualTo("5000");
    }

    @Test
    public void getProtocolWithTls() throws MalformedURLException {

        assertThat(connectionParser.getProtocol(new URL("https://petstore3.swagger.io/api/v3/"))).isEqualTo("Y");
    }

    @Test
    public void getProtocolWithNoTls() throws MalformedURLException {
        assertThat(connectionParser.getProtocol(new URL("http://petstore3.swagger.io/api/v3/"))).isEqualTo("N");
    }

    @Test
    public void getBaseUrl() throws MalformedURLException {
        assertThat(connectionParser.getBaseUrl(new URL("http://petstore3.swagger.io/api/v3/"))).isEqualTo("api/v3");
    }
}
