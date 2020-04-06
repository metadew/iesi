package io.metadew.iesi.connection.service;

import io.metadew.iesi.connection.database.DatabaseHandlerImpl;
import io.metadew.iesi.connection.database.H2Database;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.connection.database.connection.h2.H2DatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2EmbeddedDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2ServerDatabaseConnection;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class DbH2ConnectionServiceTest {

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
    void getDatabaseTestForEmbedded() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.h2",
                "description",
        Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "embedded"),
                new ConnectionParameter(new ConnectionParameterKey("test", "tst", "file"), "file"),
                new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                .collect(Collectors.toList()));
        H2Database h2Database = new H2Database(new H2EmbeddedDatabaseConnection(
                "file", "user", "password"),"schema");
        assertEquals(h2Database, DatabaseHandlerImpl.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseTestForServer() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.h2",
                "description",
                Stream.of( new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "server"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "file"), "file"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        H2Database h2Database = new H2Database(new H2ServerDatabaseConnection(
                "host", 1,"file", "user", "password"),"schema");
        assertEquals(h2Database, DatabaseHandlerImpl.getInstance().getDatabase(connection));
    }
    @Test
    void getDatabaseTestForMemory() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.h2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "memory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        H2Database h2Database = new H2Database(new H2MemoryDatabaseConnection(
                "database", "user", "password"),"schema");
        assertEquals(h2Database, DatabaseHandlerImpl.getInstance().getDatabase(connection));
    }
    @Test
    void getDatabaseWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.h2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "connectionURL"), "connectionURL"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), FrameworkCrypto.getInstance().encrypt("encrypted_password")))
                        .collect(Collectors.toList()));
        H2Database h2Database = new H2Database(new H2DatabaseConnection(
                "connectionURL", "user", "encrypted_password"),"schema");
        assertEquals(h2Database, DatabaseHandlerImpl.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.mariadb",
                "description",
                Stream.of(       new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "embedded"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "file"), "file"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingSchema() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.mariadb",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "memory"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.mariadb",
                "description",
                Stream.of(       new ConnectionParameter(new ConnectionParameterKey("test", "tst", "mode"), "embedded"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "file"), "file"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "schema"), "schema"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandlerImpl.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }
}
