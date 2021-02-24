package io.metadew.iesi.openapi;

import io.swagger.v3.oas.models.servers.Server;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


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
}
