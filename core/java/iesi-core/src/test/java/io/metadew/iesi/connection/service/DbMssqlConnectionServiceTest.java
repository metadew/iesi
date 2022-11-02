package io.metadew.iesi.connection.service;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.mssql.MssqlDatabase;
import io.metadew.iesi.connection.database.mssql.MssqlDatabaseConnection;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
public class DbMssqlConnectionServiceTest {

    @Autowired
    FrameworkCrypto frameworkCrypto;

    @SpyBean
    DatabaseHandler databaseHandler;

    @BeforeEach
    void setup() {
        Mockito.doReturn(false).when(databaseHandler).isInitializeConnectionPool(any());
    }

    @Test
    void getDatabaseTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.mssql",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "database"), "database"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));

        MssqlDatabase mssqlDatabase = new MssqlDatabase(new MssqlDatabaseConnection("host", 1, "database", "user", "password", null, "schema"), "schema");
        assertEquals(mssqlDatabase, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.mssql",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "database"), "database"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), frameworkCrypto.encrypt("encrypted_password")))
                        .collect(Collectors.toList()));
        MssqlDatabase mssqlDatabase = new MssqlDatabase(new MssqlDatabaseConnection("host", 1, "database", "user", "encrypted_password", null, "schema"), "schema");
        assertEquals(mssqlDatabase, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseMissingHost() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.mssql",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "database"), "database"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'host'", connection));
    }

    @Test
    void getDatabaseMissingPort() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.mssql",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "database"), "database"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'port'", connection));
    }

    @Test
    void getDatabaseMissingDatabase() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.mssql",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'database'", connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.mssql",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "database"), "database"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.mssql",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "database"), "database"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'password'", connection));
    }

}
