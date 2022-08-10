package io.metadew.iesi.metadata.configuration.connection.trace;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.connection.trace.ConnectionTrace;
import io.metadew.iesi.metadata.definition.connection.trace.ConnectionTraceKey;
import io.metadew.iesi.metadata.definition.connection.trace.http.HttpConnectionTrace;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConnectionTraceConfigurationTest {

    private ConnectionTrace connectionTrace1;
    private ConnectionTraceKey connectionTraceKey1;
    private ConnectionTrace connectionTrace2;
    private ConnectionTraceKey connectionTraceKey2;

    @BeforeAll
    static void prepare() {
        // Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        // Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    @BeforeEach
    void initializeTemplates() {
        connectionTraceKey1 = new ConnectionTraceKey(UUID.randomUUID());
        connectionTrace1 = HttpConnectionTrace.builder()
                .metadataKey(connectionTraceKey1)
                .runId("runId1")
                .processId(1L)
                .actionParameter("actionParemeter1")
                .name("connectionName1")
                .type("http")
                .description("description")
                .host("host1")
                .port(8080)
                .baseUrl("baseUrl1")
                .tls(true)
                .build();
        connectionTraceKey2 = new ConnectionTraceKey(UUID.randomUUID());
        connectionTrace2 = HttpConnectionTrace.builder()
                .metadataKey(connectionTraceKey2)
                .runId("runId2")
                .processId(2L)
                .actionParameter("actionParemeter2")
                .name("connectionName2")
                .type("http")
                .description("description")
                .host("host2")
                .port(8081)
                .baseUrl("baseUrl2")
                .tls(true)
                .build();
    }


    @Test
    void testGetAllEmpty() throws SQLException {
        assertThat(ConnectionTraceConfiguration.getInstance().getAll())
                .isEmpty();
    }

    @Test
    void testGetAll() throws SQLException {
        ConnectionTraceConfiguration.getInstance().insert(connectionTrace1);
        ConnectionTraceConfiguration.getInstance().insert(connectionTrace2);
        assertThat(ConnectionTraceConfiguration.getInstance().getAll())
                .containsOnly(connectionTrace1, connectionTrace2);
    }

    @Test
    void testGetById() {
        ConnectionTraceConfiguration.getInstance().insert(connectionTrace1);
        ConnectionTraceConfiguration.getInstance().insert(connectionTrace2);

        assertThat(ConnectionTraceConfiguration.getInstance().get(connectionTrace1.getMetadataKey()))
                .hasValue(connectionTrace1);
        assertThat(ConnectionTraceConfiguration.getInstance().get(connectionTrace2.getMetadataKey()))
                .hasValue(connectionTrace2);
    }

    @Test
    void testGetByIdEmpty() {
        ConnectionTraceConfiguration.getInstance().insert(connectionTrace1);

        assertThat(ConnectionTraceConfiguration.getInstance().get(new ConnectionTraceKey(UUID.randomUUID())))
                .isEmpty();
    }

    @Test
    void testInsert() {
        ConnectionTraceConfiguration.getInstance().insert(connectionTrace1);

        assertThat(ConnectionTraceConfiguration.getInstance().get(connectionTrace1.getMetadataKey()))
                .hasValue(connectionTrace1);
    }

    @Test
    void testInsertAlreadyExists() {
        ConnectionTraceConfiguration.getInstance().insert(connectionTrace1);

        assertThatThrownBy(() -> ConnectionTraceConfiguration.getInstance().insert(connectionTrace1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testUpdate() {
        ConnectionTraceConfiguration.getInstance().insert(connectionTrace1);
        connectionTrace1 = HttpConnectionTrace.builder()
                .metadataKey(connectionTraceKey1)
                .runId("runId1")
                .processId(1L)
                .actionParameter("actionParemeter2")
                .name("connectionName1")
                .type("http")
                .description("description")
                .host("host1")
                .port(8080)
                .baseUrl("baseUrl2")
                .tls(true)
                .build();
        ConnectionTraceConfiguration.getInstance().update(connectionTrace1);

        assertThat(ConnectionTraceConfiguration.getInstance().get(connectionTrace1.getMetadataKey()))
                .hasValue(connectionTrace1);
    }


}
