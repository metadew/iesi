package io.metadew.iesi.connection.service;

import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.generic.GenericDatabase;
import io.metadew.iesi.connection.database.generic.GenericDatabaseConnection;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.text.MessageFormat;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class GenericConnectionServiceTest {

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
    void getDatabaseConnectionUrlTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.generic",
                "description",
                Stream.of(
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "connectionUrl"), "connectionUrl"))
                        .collect(Collectors.toList()));

        GenericDatabase genericDatabase = new GenericDatabase(
                new GenericDatabaseConnection("connectionUrl", null, null, null));
        Assertions.assertEquals(genericDatabase, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getDatabaseConnectionUrlUserPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.generic",
                "description",
                Stream.of(
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "connectionUrl"), "connectionUrl"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));

        GenericDatabase genericDatabase = new GenericDatabase(
                new GenericDatabaseConnection("connectionUrl", "user", "password", null));
        Assertions.assertEquals(genericDatabase, DatabaseHandler.getInstance().getDatabase(connection));
    }

    @Test
    void getURLMissingTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                "db.generic",
                "description",
                Stream.of(
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                        new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> DatabaseHandler.getInstance().getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'connectionUrl'", connection));
    }

}
