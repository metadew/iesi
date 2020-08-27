package io.metadew.iesi.connection.http;

import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpConnectionDefinitionServiceTest {

    @Test
    void convertWrongTypeTest() {
        ConnectionKey connectionKey = ConnectionKey.builder()
                .name("connection1")
                .environmentKey(new EnvironmentKey("test"))
                .build();
        Connection connection = Connection.builder()
                .connectionKey(connectionKey)
                .description("description")
                .type("wrong_type")
                .parameters(
                        Stream.of(
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(connectionKey)
                                                .parameterName("host")
                                                .build())
                                        .value("value1")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(connectionKey)
                                                .parameterName("port")
                                                .build())
                                        .value("value1")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(connectionKey)
                                                .parameterName("tls")
                                                .build())
                                        .value("value1")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(connectionKey)
                                                .parameterName("baseUrl")
                                                .build())
                                        .value("value1")
                                        .build())
                                .collect(Collectors.toList()))
                .build();
        assertThatThrownBy(() -> HttpConnectionDefinitionService.getInstance().convert(connection))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot convert " + connection.toString() + " to http connection");
    }

    @Test
    void convertTest() {
        ConnectionKey tlsEnabledConnectionKey = ConnectionKey.builder()
                .name("connection1")
                .environmentKey(new EnvironmentKey("test"))
                .build();
        Connection tlsEnabledConnection = Connection.builder()
                .connectionKey(tlsEnabledConnectionKey)
                .description("description")
                .type("http")
                .parameters(
                        Stream.of(
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(tlsEnabledConnectionKey)
                                                .parameterName("host")
                                                .build())
                                        .value("host")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(tlsEnabledConnectionKey)
                                                .parameterName("port")
                                                .build())
                                        .value("1")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(tlsEnabledConnectionKey)
                                                .parameterName("tls")
                                                .build())
                                        .value("Y")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(tlsEnabledConnectionKey)
                                                .parameterName("baseUrl")
                                                .build())
                                        .value("baseUrl")
                                        .build())
                                .collect(Collectors.toList()))
                .build();
        ConnectionKey tlsDisabledConnectionKey = ConnectionKey.builder()
                .name("connection1")
                .environmentKey(new EnvironmentKey("test"))
                .build();
        Connection tlsDisabledConnection = Connection.builder()
                .connectionKey(tlsDisabledConnectionKey)
                .description("description")
                .type("http")
                .parameters(
                        Stream.of(
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(tlsDisabledConnectionKey)
                                                .parameterName("host")
                                                .build())
                                        .value("host")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(tlsDisabledConnectionKey)
                                                .parameterName("port")
                                                .build())
                                        .value("1")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(tlsDisabledConnectionKey)
                                                .parameterName("tls")
                                                .build())
                                        .value("N")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(tlsDisabledConnectionKey)
                                                .parameterName("baseUrl")
                                                .build())
                                        .value("baseUrl")
                                        .build())
                                .collect(Collectors.toList()))
                .build();

        assertThat(HttpConnectionDefinitionService.getInstance().convert(tlsEnabledConnection))
                .as("Http connection definition with all parameters explicitly defined. Tls is true")
                .isEqualTo(new HttpConnectionDefinition(
                        "connection1",
                        "description",
                        "test",
                        "host",
                        "baseUrl",
                        1,
                        true
                ));

        assertThat(HttpConnectionDefinitionService.getInstance().convert(tlsDisabledConnection))
                .as("Http connection definition with all parameters explicitly defined. Tls is false")
                .isEqualTo(new HttpConnectionDefinition(
                        "connection1",
                        "description",
                        "test",
                        "host",
                        "baseUrl",
                        1,
                        false
                ));


    }

    @Test
    void convertTestNoHost() {
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

        assertThatThrownBy(() -> HttpConnectionDefinitionService.getInstance().convert(connection))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Http connection " + connection.toString() + " does not contain a host");
    }

    @Test
    void convertTestNoTls() {
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
                                                .parameterName("baseUrl")
                                                .build())
                                        .value("baseUrl")
                                        .build())
                                .collect(Collectors.toList()))
                .build();

        assertThatThrownBy(() -> HttpConnectionDefinitionService.getInstance().convert(connection))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Http connection " + connection.toString() + " does not contain a tls setting");
    }



    @Test
    void convertTestNoPort() {
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

        assertThat(HttpConnectionDefinitionService.getInstance().convert(connection))
                .isEqualTo(new HttpConnectionDefinition(
                        "connection1",
                        "description",
                        "test",
                        "host",
                        "baseUrl",
                        null,
                        true
                ));
    }

    @Test
    void convertTestNoBaseUrl() {
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
                                                .parameterName("tls")
                                                .build())
                                        .value("Y")
                                        .build(),
                                ConnectionParameter.builder()
                                        .connectionParameterKey(ConnectionParameterKey.builder()
                                                .connectionKey(connectionKey)
                                                .parameterName("port")
                                                .build())
                                        .value("1")
                                        .build())
                                .collect(Collectors.toList()))
                .build();

        assertThat(HttpConnectionDefinitionService.getInstance().convert(connection))
                .isEqualTo(new HttpConnectionDefinition(
                        "connection1",
                        "description",
                        "test",
                        "host",
                        null,
                        1,
                        true
                ));
    }


}
