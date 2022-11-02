package io.metadew.iesi.connection.service;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.oracle.OracleDatabase;
import io.metadew.iesi.connection.database.oracle.OracleDatabaseConnection;
import io.metadew.iesi.connection.database.oracle.ServiceNameOracleDatabaseConnection;
import io.metadew.iesi.connection.database.oracle.TnsAliasOracleDatabaseConnection;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
class DbOracleConnectionServiceTest {

    @Autowired
    FrameworkCrypto frameworkCrypto;

    @SpyBean
    DatabaseHandler databaseHandler;

    @BeforeEach
    void setup() {
        Mockito.doReturn(false).when(databaseHandler).isInitializeConnectionPool(any());
    }

    @Test
    void getDatabaseServiceTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "service"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "service"), "service"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        OracleDatabase oracleDatabaseExpected = new OracleDatabase(new ServiceNameOracleDatabaseConnection("host", 1, "service", "user", "password", null, "schema"), "schema");
        assertEquals(oracleDatabaseExpected, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseTnsTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tnsAlias"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        OracleDatabase oracleDatabaseExpected = new OracleDatabase(new TnsAliasOracleDatabaseConnection("host", 1, "tnsAlias", "user", "password", null, "schema"), "schema");
        assertEquals(oracleDatabaseExpected, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseConnectionUrlTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "connectionUrl"), "connectionUrl"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        OracleDatabase oracleDatabaseExpected = new OracleDatabase(new OracleDatabaseConnection("connectionUrl", "user", "password", null, "schema"), "schema");
        assertEquals(oracleDatabaseExpected, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseTnsWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tnsAlias"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), frameworkCrypto.encrypt("encrypted_password")),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        OracleDatabase oracleDatabaseExpected = new OracleDatabase(new TnsAliasOracleDatabaseConnection("host", 1, "tnsAlias", "user", "encrypted_password", null, "schema"), "schema");
        assertEquals(oracleDatabaseExpected, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseServiceWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "service"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "service"), "service"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), frameworkCrypto.encrypt("encrypted_password")),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        OracleDatabase oracleDatabaseExpected = new OracleDatabase(new ServiceNameOracleDatabaseConnection("host", 1, "service", "user", "encrypted_password", null, "schema"), "schema");
        assertEquals(oracleDatabaseExpected, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseConnectionUrlWithEncryptedPasswordTest() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "connectionUrl"), "connectionUrl"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), frameworkCrypto.encrypt("encrypted_password")),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        OracleDatabase oracleDatabaseExpected = new OracleDatabase(new OracleDatabaseConnection("connectionUrl", "user", "encrypted_password", null, "schema"), "schema");
        assertEquals(oracleDatabaseExpected, databaseHandler.getDatabase(connection));
    }

    @Test
    void getDatabaseMissingHost() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tnsAlias"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'host'", connection));
    }

    @Test
    void getDatabaseMissingPort() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tnsAlias"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'port'", connection));
    }


    @Test
    void getDatabaseMissingUser() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tnsAlias"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'user'", connection));
    }

    @Test
    void getDatabaseMissingPassword() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "tnsAlias"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Connection {0} does not contain mandatory parameter 'password'", connection));
    }

    @Test
    void getDatabaseUnknownMode() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "mode"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Oracle database {0} does not know mode 'mode'", connection));
    }

    @Test
    void getDatabaseMissingTnsAlias() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "tnsAlias"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Oracle database {0} does not know mode 'tnsAlias'", connection));
    }

    @Test
    void getDatabaseMissingService() {
        Connection connection = new Connection(new ConnectionKey("test", "tst"),
                new SecurityGroupKey(UUID.randomUUID()),
                "PUBLIC",
                "db.oracle",
                "description",
                Stream.of(
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "host"), "host"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "port"), "1"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "mode"), "service"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "user"), "user"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "password"), "password"),
                                new ConnectionParameter(new ConnectionParameterKey(new ConnectionKey("test", "tst"), "schema"), "schema"))
                        .collect(Collectors.toList()));
        assertThrows(RuntimeException.class, () -> databaseHandler.getDatabase(connection),
                MessageFormat.format("Oracle database {0} does not know mode 'tnsAlias'", connection));
    }
}
