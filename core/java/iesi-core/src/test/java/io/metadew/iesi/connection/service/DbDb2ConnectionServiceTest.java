package io.metadew.iesi.connection.service;

import io.metadew.iesi.connection.database.Db2Database;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionHandlerImpl;
import io.metadew.iesi.connection.database.connection.db2.Db2DatabaseConnection;
import io.metadew.iesi.connection.operation.DbDb2ConnectionService;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
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

public class DbDb2ConnectionServiceTest {
    @BeforeAll
    static void setup()  {
        DatabaseConnectionHandlerImpl databaseConnectionHandler = PowerMockito.mock(DatabaseConnectionHandlerImpl.class);
        Whitebox.setInternalState(DatabaseConnectionHandlerImpl.class, "INSTANCE", databaseConnectionHandler);
        Mockito.doReturn(null).when(databaseConnectionHandler).getConnection(any());
    }

    @Test
    void getDatabaseTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.db2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        Db2Database db2Database = new Db2Database(new Db2DatabaseConnection("host", 1, "database", "user", "password"),"schema");
        assertEquals(db2Database, DbDb2ConnectionService.getInstance().getDatabase(connection));
    }
    @Test
    void getDatabaseWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.db2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), FrameworkCrypto.getInstance().encrypt("encrypted_password")))
                        .collect(Collectors.toList()));
        Db2Database db2Database = new Db2Database(new Db2DatabaseConnection("host", 1, "database", "user", "encrypted_password"),"schema");
        assertEquals(db2Database, DbDb2ConnectionService.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseMissingHost() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.db2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbDb2ConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'host'", connection));
    }

    @Test
    void getDatabaseMissingPort() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.db2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbDb2ConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'port'", connection));
    }

    @Test
    void getDatabaseMissingDatabase() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.db2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbDb2ConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'database'", connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.db2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbDb2ConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.db2",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey("test", "tst", "host"), "host"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "port"), "1"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "database"), "database"),
                        new ConnectionParameter(new ConnectionParameterKey("test", "tst", "user"), "user"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DbDb2ConnectionService.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'password'", connection));
    }

}
