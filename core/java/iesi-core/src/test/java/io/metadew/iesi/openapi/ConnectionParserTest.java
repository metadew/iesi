package io.metadew.iesi.openapi;

import io.swagger.v3.oas.models.servers.Server;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ConnectionParserTest {



    @Test
    public void getAdresses() throws MalformedURLException {
        ConnectionParser connectionParser = mock(ConnectionParser.class);
        List<Server> servers = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        Server server = new Server();
        server.setUrl("https://petstore3.swagger.io/api/v3/");
        URL url = new URL(server.getUrl());

        servers.add(server);
        urls.add(url);

        when(connectionParser.getAdresses(servers)).thenReturn(urls);
    }

    @Test
    public void getAdresseWithWrongUrl() {
        ConnectionParser connectionParser = mock(ConnectionParser.class);
        List<Server> servers = new ArrayList<>();
        Server server = new Server();
        server.setUrl("https//petstore3.swagger/api/v3/");

        servers.add(server);


        when(connectionParser.getAdresses(servers)).thenReturn(new ArrayList<>());
    }

    @Test
    public void getHost() throws MalformedURLException {
        ConnectionParser connectionParser = ConnectionParser.getInstance();

        assertThat(connectionParser.getHost(new URL("https://petstore3.swagger.io/api/v3/"))).isEqualTo("petstore3.swagger.io");
    }

    @Test
    public void getPortWithNoPortDefined() throws MalformedURLException {
        ConnectionParser connectionParser = ConnectionParser.getInstance();


        assertThat(connectionParser.getPort(new URL("https://petstore3.swagger.io/api/v3/"))).isEqualTo(null);

    }

    @Test
    public void getPortWithPortDefined() throws MalformedURLException {
        ConnectionParser connectionParser = ConnectionParser.getInstance();


        assertThat(connectionParser.getPort(new URL("http://petstore3.swagger.io:5000/api/v3/"))).isEqualTo("5000");

    }
}
