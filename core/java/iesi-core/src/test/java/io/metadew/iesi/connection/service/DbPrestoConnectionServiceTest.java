package io.metadew.iesi.connection.service;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.presto.PrestoDatabase;
import io.metadew.iesi.connection.database.presto.PrestoDatabaseConnection;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


class DbPrestoConnectionServiceTest {

    @BeforeAll
    static void setup() {
        DatabaseHandler databaseConnectionHandler = DatabaseHandler.getInstance();
        DatabaseHandler databaseConnectionHandlerSpy = Mockito.spy(databaseConnectionHandler);
        Whitebox.setInternalState(DatabaseHandler.class, "INSTANCE", databaseConnectionHandlerSpy);
        Mockito.doReturn(false).when(databaseConnectionHandlerSpy).isInitializeConnectionPool(any());
    }

    @AfterAll
    static void destroy() {
        Whitebox.setInternalState(DatabaseHandler.class, "INSTANCE", (DatabaseHandler) null);
    }

    @Test
    void getDatabaseTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        PrestoDatabase prestoDatabaseExpected = new PrestoDatabase(new PrestoDatabaseConnection("host",
                1,
                "catalog",
                "schema",
                "user",
                "password"), "schema");
        Assert.assertEquals(prestoDatabaseExpected, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), FrameworkCrypto.getInstance().encrypt("encrypted_password")))
                        .collect(Collectors.toList()));
        PrestoDatabase prestoDatabaseExpected = new PrestoDatabase(new PrestoDatabaseConnection("host",
                1,
                "catalog",
                "schema",
                "user",
                "encrypted_password"), "schema");
        Assert.assertEquals(prestoDatabaseExpected, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseMissingHost() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'host'", connection));
    }

    @Test
    void getDatabaseMissingPort() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'port'", connection));
    }

    @Test
    void getDatabaseMissingSchema() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'schema'", connection));
    }

    @Test
    void getDatabaseMissingCatalog() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'catalog'", connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'password'", connection));
    }

}