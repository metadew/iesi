package io.metadew.iesi.connection.service;

import io.metadew.iesi.connection.database.DatabaseHandlerImpl;
import io.metadew.iesi.connection.database.DrillDatabase;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.connection.database.connection.drill.DrillDatabaseConnection;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import java.text.MessageFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class DbDrillConnectionServiceTest {

    @BeforeAll
    static void setup()  {
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
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));

        DrillDatabase drillDatabase = new DrillDatabase(new DrillDatabaseConnection("mode","cluster", "directory", "clusterId", "schema","tries","user","password"),"schema");
        assertEquals(drillDatabase, DatabaseHandlerImpl.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"),  FrameworkCrypto.getInstance().encrypt("encrypted_password")))
                        .collect(Collectors.toList()));

        DrillDatabase drillDatabase = new DrillDatabase(new DrillDatabaseConnection("mode","cluster", "directory", "clusterId", "schema","tries","user","encrypted_password"),"schema");
        assertEquals(drillDatabase, DatabaseHandlerImpl.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseMissingMode() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));


        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'mode'", connection));
    }

    @Test
    void getDatabaseMissingCluster() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));


        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'cluster'", connection));
    }

    @Test
    void getDatabaseMissingDirectory() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));


        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'directory'", connection));
    }

    @Test
    void getDatabaseMissingClusterid() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'clusterId'", connection));
    }

    @Test
    void getDatabaseMissingSchema() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'schema'", connection));
    }

    @Test
    void getDatabaseMissingTries() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'tries'", connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "mode"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "cluster"), "cluster"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "directory"), "directory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "clusterId"), "clusterId"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "tries"), "tries"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"))
                        .collect(Collectors.toList()));

        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'password'", connection));
    }
}
