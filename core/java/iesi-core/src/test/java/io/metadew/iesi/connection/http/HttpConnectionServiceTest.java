package io.metadew.iesi.connection.http;

import io.metadew.iesi.metadata.configuration.Configuration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {Configuration.class, HttpConnectionService.class, ConnectionConfiguration.class })
class HttpConnectionServiceTest {

    @Autowired
    private HttpConnectionService httpConnectionService;

    @SpyBean
    private ConnectionConfiguration connectionConfigurationSpy;

    @Test
    void getBaseUrlTest() {
        Assertions.assertThat(httpConnectionService.getBaseUri(new HttpConnection(
                "referenceName",
                "description",
                "test",
                "host.com",
                "baseUrl",
                8080,
                true
        ))).isEqualTo("https://host.com:8080/baseUrl");
        assertThat(httpConnectionService.getBaseUri(new HttpConnection(
                "referenceName",
                "description",
                "test",
                "host.com",
                "baseUrl",
                8080,
                false
        ))).isEqualTo("http://host.com:8080/baseUrl");
    }

    @Test
    void getBaseUrlNoPortTest() {
        assertThat(httpConnectionService.getBaseUri(new HttpConnection(
                "referenceName",
                "description",
                "test",
                "host.com",
                "baseUrl",
                null,
                true
        ))).isEqualTo("https://host.com/baseUrl");
    }

    @Test
    void getBaseUrlNoBaseUrlTest() {
        assertThat(httpConnectionService.getBaseUri(new HttpConnection(
                "referenceName",
                "description",
                "test",
                "host.com",
                null,
                8080,
                true
        ))).isEqualTo("https://host.com:8080");
    }

    @Test
    void getTest() {
        ConnectionKey connectionKey = ConnectionKey.builder()
                .name("connection1")
                .environmentKey(new EnvironmentKey("test"))
                .build();
        Connection connection = Connection.builder()
                .connectionKey(connectionKey)
                .description("description")
                .type("http")
                .parameters(
                        Stream.of(
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(connectionKey)
                                                .parameterName("host")
                                                .build())
                                        .value("host")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(connectionKey)
                                                .parameterName("port")
                                                .build())
                                        .value("1")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(connectionKey)
                                                .parameterName("tls")
                                                .build())
                                        .value("Y")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(connectionKey)
                                                .parameterName("baseUrl")
                                                .build())
                                        .value("baseUrl")
                                        .build())
                                .collect(Collectors.toList()))
                .build();

        doReturn(Optional.of(connection))
                .when(connectionConfigurationSpy)
                .get(new ConnectionKey("connection1", new EnvironmentKey("test")));
        ActionExecution actionExecution = mock(ActionExecution.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        when(executionControl.getEnvName())
                .thenReturn("test");
        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);

        assertThat(httpConnectionService.get("connection1", actionExecution))
                .isEqualTo(new HttpConnection("connection1", "description", "test", "host", "baseUrl", 1, true));
    }

    @Test
    void getTestNotFound() {
        doReturn(Optional.empty())
                .when(connectionConfigurationSpy)
                .get(new ConnectionKey("connection1", new EnvironmentKey("test")));
        ActionExecution actionExecution = mock(ActionExecution.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        when(executionControl.getEnvName())
                .thenReturn("test");
        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);

        assertThatThrownBy(() -> httpConnectionService.get("connection1", actionExecution))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Could not find definition for http connection connection1 for environment test");
    }

}
