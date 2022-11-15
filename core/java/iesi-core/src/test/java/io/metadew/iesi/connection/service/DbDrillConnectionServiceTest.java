package io.metadew.iesi.connection.service;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.drill.DrillDatabase;
import io.metadew.iesi.connection.database.drill.DrillDatabaseConnection;
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
@DirtiesContext()
class DbDrillConnectionServiceTest {

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
        assertEquals(drillDatabase, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.drill",
                "description",
                Stream.of(new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "cluster"), "cluster"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "directory"), "directory"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "clusterId"), "clusterId"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tries"), "tries"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), frameworkCrypto.encrypt("encrypted_password")))
                        .collect(Collectors.toList()));

        DrillDatabase drillDatabase = new DrillDatabase(new DrillDatabaseConnection("mode", "cluster", "directory", "clusterId", "schema", "tries", "user", "encrypted_password"), "schema");
        assertEquals(drillDatabase, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseMissingMode() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
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


        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'mode'", connection));
    }

    @Test
    void getDatabaseMissingCluster() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
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


        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'cluster'", connection));
    }

    @Test
    void getDatabaseMissingDirectory() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
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


        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'directory'", connection));
    }

    @Test
    void getDatabaseMissingClusterid() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
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

        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'clusterId'", connection));
    }

    @Test
    void getDatabaseMissingSchema() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
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

        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'schema'", connection));
    }

    @Test
    void getDatabaseMissingTries() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
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

        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'tries'", connection));
    }

    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
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

        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
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

        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'password'", connection));
    }
}
