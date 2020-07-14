package io.metadew.iesi.connection.service;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.drill.DrillDatabase;
import io.metadew.iesi.connection.database.drill.DrillDatabaseConnection;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class DbDrillConnectionServiceTest {

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
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));

        DrillDatabase drillDatabase = new DrillDatabase(new DrillDatabaseConnection("mode", "cluster", "directory", "clusterId", "schema", "tries", "user", "password"), "schema");
        assertEquals(drillDatabase, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseWithEncryptedPasswordTest() throws Exception {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), FrameworkCrypto.getInstance().encrypt("encrypted_password")))
                        .collect(Collectors.toList()));

        DrillDatabase drillDatabase = new DrillDatabase(new DrillDatabaseConnection("mode", "cluster", "directory", "clusterId", "schema", "tries", "user", "encrypted_password"), "schema");
        assertEquals(drillDatabase, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseMissingMode() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));


        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'mode'", connection));
    }

    @Test
    void getDatabaseMissingCluster() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));


        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'cluster'", connection));
    }

    @Test
    void getDatabaseMissingDirectory() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));


        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'directory'", connection));
    }

    @Test
    void getDatabaseMissingClusterid() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'clusterId'", connection));
    }

    @Test
    void getDatabaseMissingSchema() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'schema'", connection));
    }

    @Test
    void getDatabaseMissingTries() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'tries'", connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'password'", connection));
    }
}
