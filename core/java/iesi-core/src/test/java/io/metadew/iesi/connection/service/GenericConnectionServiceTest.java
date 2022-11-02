package io.metadew.iesi.connection.service;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.generic.GenericDatabase;
import io.metadew.iesi.connection.database.generic.GenericDatabaseConnection;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
class GenericConnectionServiceTest {

    @SpyBean
    DatabaseHandler databaseHandler;

    @BeforeEach
    void setup() {
        Mockito.doReturn(false).when(databaseHandler).isInitializeConnectionPool(any());
    }

    @Test
    void getDatabaseConnectionUrlTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.generic",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "connectionUrl"), "connectionUrl"))
                        .collect(Collectors.toList()));

        GenericDatabase genericDatabase = new GenericDatabase(
                new GenericDatabaseConnection("connectionUrl", null, null, null));
        Assertions.assertEquals(genericDatabase, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseConnectionUrlUserPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.generic",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "connectionUrl"), "connectionUrl"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));

        GenericDatabase genericDatabase = new GenericDatabase(
                new GenericDatabaseConnection("connectionUrl", "user", "password", null));
        Assertions.assertEquals(genericDatabase, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseConnectionUrlSchemaTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.generic",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "connectionUrl"), "connectionUrl"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));

        GenericDatabase genericDatabase = new GenericDatabase(
                new GenericDatabaseConnection("connectionUrl", null, null, null, "schema"));
        Assertions.assertEquals(genericDatabase, databaseHandler.getDatabase(connection));
    }

    @Test
    void getURLMissingTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.generic",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'connectionUrl'", connection));
    }

}
