package io.metadew.iesi.connection.service;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.h2.H2Database;
import io.metadew.iesi.connection.database.h2.H2DatabaseConnection;
import io.metadew.iesi.connection.database.h2.H2EmbeddedDatabaseConnection;
import io.metadew.iesi.connection.database.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.database.h2.H2ServerDatabaseConnection;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class DbH2ConnectionServiceTest {

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
    void getDatabaseTestForEmbedded() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.h2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "embedded"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "file"), "file"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        H2Database h2Database = new H2Database(new H2EmbeddedDatabaseConnection(
                "file", "user", "password", null, "schema"), "schema");
        assertEquals(h2Database, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseTestForServer() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.h2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "server"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "file"), "file"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        H2Database h2Database = new H2Database(new H2ServerDatabaseConnection(
                "host", 1, "file", "user", "password", null, "schema"), "schema");
        assertEquals(h2Database, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseTestForMemory() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.h2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "memory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        H2Database h2Database = new H2Database(new H2MemoryDatabaseConnection(
                "database", "user", "password", null, "schema"), "schema");
        assertEquals(h2Database, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.h2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "connectionURL"), "connectionURL"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), FrameworkCrypto.getInstance().encrypt("encrypted_password")))
                        .collect(Collectors.toList()));
        H2Database h2Database = new H2Database(new H2DatabaseConnection(
                "connectionURL", "user", "encrypted_password", null, "schema"), "schema");
        assertEquals(h2Database, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.mariadb",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "embedded"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "file"), "file"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingSchema() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.mariadb",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "memory"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
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
                "db.mariadb",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "embedded"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "file"), "file"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }
}
