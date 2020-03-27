package io.metadew.iesi.connection.service;

import io.metadew.iesi.connection.database.DatabaseHandlerImpl;
import io.metadew.iesi.connection.database.PrestoDatabase;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.connection.database.connection.presto.PrestoDatabaseConnection;
import io.metadew.iesi.connection.operation.DbPrestoConnectionService;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.text.MessageFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.doubleThat;


class DbPrestoConnectionServiceTest {

    @BeforeAll
    static void setup() {
        DatabaseConnectionHandlerImpl databaseConnectionHandler = PowerMockito.mock(DatabaseConnectionHandlerImpl.class);
        Whitebox.setInternalState(DatabaseConnectionHandlerImpl.class, "INSTANCE", databaseConnectionHandler);
        Mockito.doReturn(null).when(databaseConnectionHandler).getConnection(any());
    }

    @AfterAll
    static void destroy() {
        Whitebox.setInternalState(DatabaseConnectionHandlerImpl.class, "INSTANCE", (DatabaseConnectionHandlerImpl) null);
    }

    @Test
    void getDatabaseTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        PrestoDatabase prestoDatabaseExpected = new PrestoDatabase(new PrestoDatabaseConnection("host",
                1,
                "catalog",
                "schema",
                "user",
                "password"), "");
        Assert.assertEquals(prestoDatabaseExpected, DbPrestoConnectionService.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), FrameworkCrypto.getInstance().encrypt("encrypted_password")))
                        .collect(Collectors.toList()));
        PrestoDatabase prestoDatabaseExpected = new PrestoDatabase(new PrestoDatabaseConnection("host",
                1,
                "catalog",
                "schema",
                "user",
                "encrypted_password"), "");
        Assert.assertEquals(prestoDatabaseExpected, DbPrestoConnectionService.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseMissingHost() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbPrestoConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'host'", connection));
    }

    @Test
    void getDatabaseMissingPort() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbPrestoConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'port'", connection));
    }

    @Test
    void getDatabaseMissingSchema() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbPrestoConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'schema'", connection));
    }

    @Test
    void getDatabaseMissingCatalog() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbPrestoConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'catalog'", connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbPrestoConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.presto",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "catalog"), "catalog"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbPrestoConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'password'", connection));
    }

}