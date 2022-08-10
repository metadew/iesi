package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionConfigurationTest {

    private Connection connection1;
    private Connection connection2;
    private Connection connection3;
    private ConnectivityMetadataRepository connectivityMetadataRepository;

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
    void setup() {
        SecurityGroupKey securityGroupKey = new SecurityGroupKey(UUID.randomUUID());
        connection1 = new ConnectionBuilder("conn1", "env1")
                .description("desc")
                .securityGroupName("PUBLIC")
                .securityGroupKey(securityGroupKey)
                .numberOfParameters(2)
                .build();
        connection2 = new ConnectionBuilder("conn1", "env2")
                .description("desc")
                .securityGroupName("PUBLIC")
                .securityGroupKey(securityGroupKey)
                .numberOfParameters(2)
                .build();
        connection3 = new ConnectionBuilder("conn2", "env1")
                .description("desc")
                .securityGroupName("PUBLIC")
                .securityGroupKey(securityGroupKey)
                .numberOfParameters(2)
                .build();

        connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
    }

    @Test
    void connectionNotExistsTest() {
        assertFalse(ConnectionConfiguration.getInstance().exists(connection1));
    }

    @Test
    void connectionExistsTest() {
        ConnectionConfiguration.getInstance().insert(connection1);
        assertTrue(ConnectionConfiguration.getInstance().exists(connection1.getMetadataKey()));
    }

    @Test
    void connectionInsertTest() {
        ConnectionConfiguration.getInstance().insert(connection1);

        Optional<Connection> fetchedConnection = ConnectionConfiguration.getInstance().get(connection1.getMetadataKey());

        assertTrue(fetchedConnection.isPresent());
        assertEquals(connection1, fetchedConnection.get());
    }

    @Test
    void connectionInsertMultipleTest() {
        ConnectionConfiguration.getInstance().insert(connection1);
        ConnectionConfiguration.getInstance().insert(connection2);
        ConnectionConfiguration.getInstance().insert(connection3);

        Optional<Connection> fetchedConnection1 = ConnectionConfiguration.getInstance().get(connection1.getMetadataKey());
        Optional<Connection> fetchedConnection2 = ConnectionConfiguration.getInstance().get(connection2.getMetadataKey());
        Optional<Connection> fetchedConnection3 = ConnectionConfiguration.getInstance().get(connection3.getMetadataKey());

        assertEquals(3, ConnectionConfiguration.getInstance().getAll().size());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals(connection1, fetchedConnection1.get());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals(connection2, fetchedConnection2.get());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals(connection3, fetchedConnection3.get());
    }

    @Test
    void connectionInsertAlreadyExistsTest() {
        ConnectionConfiguration.getInstance().insert(connection1);
        assertThrows(MetadataAlreadyExistsException.class, () -> ConnectionConfiguration.getInstance().insert(connection1));
    }

    @Test
    void connectionDeleteTest() {
        ConnectionConfiguration.getInstance().insert(connection1);

        assertEquals(1, ConnectionConfiguration.getInstance().getAll().size());

        ConnectionConfiguration.getInstance().delete(connection1.getMetadataKey());

        assertEquals(0, ConnectionConfiguration.getInstance().getAll().size());
        assertEquals(0, ConnectionParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    void connectionDeleteMultipleTest() {
        ConnectionConfiguration.getInstance().insert(connection1);
        ConnectionConfiguration.getInstance().insert(connection2);
        ConnectionConfiguration.getInstance().insert(connection3);

        assertEquals(3, ConnectionConfiguration.getInstance().getAll().size());

        ConnectionConfiguration.getInstance().delete(connection1.getMetadataKey());

        assertEquals(2, ConnectionConfiguration.getInstance().getAll().size());
        assertEquals(4, ConnectionParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    void connectionDeleteMultiple2Test() {
        ConnectionConfiguration.getInstance().insert(connection1);
        ConnectionConfiguration.getInstance().insert(connection2);
        ConnectionConfiguration.getInstance().insert(connection3);

        assertEquals(3, ConnectionConfiguration.getInstance().getAll().size());

        ConnectionConfiguration.getInstance().delete(connection2.getMetadataKey());

        assertEquals(2, ConnectionConfiguration.getInstance().getAll().size());
        assertEquals(4, ConnectionParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    void connectionDeleteMultiple3Test() {
        ConnectionConfiguration.getInstance().insert(connection1);
        ConnectionConfiguration.getInstance().insert(connection2);
        ConnectionConfiguration.getInstance().insert(connection3);

        assertEquals(3, ConnectionConfiguration.getInstance().getAll().size());

        ConnectionConfiguration.getInstance().delete(connection3.getMetadataKey());

        assertEquals(2, ConnectionConfiguration.getInstance().getAll().size());
        assertEquals(4, ConnectionParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    void connectionDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> ConnectionConfiguration.getInstance().delete(connection1.getMetadataKey()));
    }


    @Test
    void connectionGetNotExistsTest() {
        assertFalse(ConnectionConfiguration.getInstance().exists(connection1));
        assertFalse(ConnectionConfiguration.getInstance().get(connection1.getMetadataKey()).isPresent());
    }

    @Test
    void connectionUpdate1Test() {
        ConnectionConfiguration.getInstance().insert(connection1);
        ConnectionConfiguration.getInstance().insert(connection2);
        ConnectionConfiguration.getInstance().insert(connection3);

        Optional<Connection> fetchedConnection1 = ConnectionConfiguration.getInstance().get(connection1.getMetadataKey());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals("desc", fetchedConnection1.get().getDescription());
        Optional<Connection> fetchedConnection2 = ConnectionConfiguration.getInstance().get(connection2.getMetadataKey());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals("desc", fetchedConnection2.get().getDescription());
        Optional<Connection> fetchedConnection3 = ConnectionConfiguration.getInstance().get(connection3.getMetadataKey());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals("desc", fetchedConnection3.get().getDescription());

        connection1.setDescription("new desc");
        ConnectionConfiguration.getInstance().update(connection1);

        fetchedConnection1 = ConnectionConfiguration.getInstance().get(connection1.getMetadataKey());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals("new desc", fetchedConnection1.get().getDescription());
        fetchedConnection2 = ConnectionConfiguration.getInstance().get(connection2.getMetadataKey());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals("new desc", fetchedConnection2.get().getDescription());
        fetchedConnection3 = ConnectionConfiguration.getInstance().get(connection3.getMetadataKey());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals("desc", fetchedConnection3.get().getDescription());
    }

    @Test
    void connectionUpdate2Test() {
        ConnectionConfiguration.getInstance().insert(connection1);
        ConnectionConfiguration.getInstance().insert(connection2);
        ConnectionConfiguration.getInstance().insert(connection3);

        Optional<Connection> fetchedConnection1 = ConnectionConfiguration.getInstance().get(connection1.getMetadataKey());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals("desc", fetchedConnection1.get().getDescription());
        Optional<Connection> fetchedConnection2 = ConnectionConfiguration.getInstance().get(connection2.getMetadataKey());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals("desc", fetchedConnection2.get().getDescription());
        Optional<Connection> fetchedConnection3 = ConnectionConfiguration.getInstance().get(connection3.getMetadataKey());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals("desc", fetchedConnection3.get().getDescription());

        connection3.setDescription("new desc");
        ConnectionConfiguration.getInstance().update(connection3);

        fetchedConnection1 = ConnectionConfiguration.getInstance().get(connection1.getMetadataKey());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals("desc", fetchedConnection1.get().getDescription());
        fetchedConnection2 = ConnectionConfiguration.getInstance().get(connection2.getMetadataKey());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals("desc", fetchedConnection2.get().getDescription());
        fetchedConnection3 = ConnectionConfiguration.getInstance().get(connection3.getMetadataKey());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals("new desc", fetchedConnection3.get().getDescription());
    }

}