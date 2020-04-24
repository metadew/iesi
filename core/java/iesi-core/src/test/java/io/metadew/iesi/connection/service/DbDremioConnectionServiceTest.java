package io.metadew.iesi.connection.service;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.dremio.DremioDatabase;
import io.metadew.iesi.connection.database.dremio.DremioDatabaseConnection;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.text.MessageFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class DbDremioConnectionServiceTest {

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
    void getDatabaseTestForZookeeperMode() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "zookeeper"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        DremioDatabase dremioDatabase = new DremioDatabase(new DremioDatabaseConnection("host", 1, "zookeeper", "cluster", "schema", "user", "password"), "schema");
        assertEquals(dremioDatabase, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseTestForDirectMode() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "direct"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        DremioDatabase dremioDatabase = new DremioDatabase(new DremioDatabaseConnection("host", 1, "direct", "cluster", "schema", "user", "password"), "schema");
        assertEquals(dremioDatabase, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseTestWithoutMode() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'mode'", connection));
    }

    @Test
    void getDatabaseWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "direct"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), FrameworkCrypto.getInstance().encrypt("encrypted_password")))
                        .collect(Collectors.toList()));
        DremioDatabase dremioDatabase = new DremioDatabase(new DremioDatabaseConnection("host", 1, "direct", "cluster", "schema", "user", "encrypted_password"), "schema");
        assertEquals(dremioDatabase, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseMissingHost() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "zookeeper"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
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
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "zookeeper"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'port'", connection));
    }

    @Test
    void getDatabaseMissinMode() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'mode'", connection));
    }

    @Test
    void getDatabaseMissingCluster() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'cluster'", connection));
    }

    @Test
    void getDatabaseMissingSchema() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'schema'", connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.dremio",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'password'", connection));
    }
}
