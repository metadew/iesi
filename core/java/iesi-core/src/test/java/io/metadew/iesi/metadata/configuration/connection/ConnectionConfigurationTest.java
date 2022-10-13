package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, ConnectionConfiguration.class, ConnectionParameterConfiguration.class })
@ActiveProfiles("test")
class ConnectionConfigurationTest {

    Connection connection1;
    Connection connection2;
    Connection connection3;

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    ConnectionConfiguration connectionConfiguration;
    @Autowired
    ConnectionParameterConfiguration connectionParameterConfiguration;

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

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }


    @Test
    void connectionNotExistsTest() {
        assertFalse(connectionConfiguration.exists(connection1));
    }

    @Test
    void connectionExistsTest() {
        connectionConfiguration.insert(connection1);
        assertTrue(connectionConfiguration.exists(connection1.getMetadataKey()));
    }

    @Test
    void connectionInsertTest() {
        connectionConfiguration.insert(connection1);

        Optional<Connection> fetchedConnection = connectionConfiguration.get(connection1.getMetadataKey());

        assertTrue(fetchedConnection.isPresent());
        assertEquals(connection1, fetchedConnection.get());
    }

    @Test
    void connectionInsertMultipleTest() {
        connectionConfiguration.insert(connection1);
        connectionConfiguration.insert(connection2);
        connectionConfiguration.insert(connection3);

        Optional<Connection> fetchedConnection1 = connectionConfiguration.get(connection1.getMetadataKey());
        Optional<Connection> fetchedConnection2 = connectionConfiguration.get(connection2.getMetadataKey());
        Optional<Connection> fetchedConnection3 = connectionConfiguration.get(connection3.getMetadataKey());

        assertEquals(3, connectionConfiguration.getAll().size());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals(connection1, fetchedConnection1.get());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals(connection2, fetchedConnection2.get());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals(connection3, fetchedConnection3.get());
    }

    @Test
    void connectionInsertAlreadyExistsTest() {
        connectionConfiguration.insert(connection1);
        assertThrows(MetadataAlreadyExistsException.class, () -> connectionConfiguration.insert(connection1));
    }

    @Test
    void connectionDeleteTest() {
        connectionConfiguration.insert(connection1);

        assertEquals(1, connectionConfiguration.getAll().size());

        connectionConfiguration.delete(connection1.getMetadataKey());

        assertEquals(0, connectionConfiguration.getAll().size());
        assertEquals(0, connectionParameterConfiguration.getAll().size());
    }

    @Test
    void connectionDeleteMultipleTest() {
        connectionConfiguration.insert(connection1);
        connectionConfiguration.insert(connection2);
        connectionConfiguration.insert(connection3);

        assertEquals(3, connectionConfiguration.getAll().size());

        connectionConfiguration.delete(connection1.getMetadataKey());

        assertEquals(2, connectionConfiguration.getAll().size());
        assertEquals(4, connectionParameterConfiguration.getAll().size());
    }

    @Test
    void connectionDeleteMultiple2Test() {
        connectionConfiguration.insert(connection1);
        connectionConfiguration.insert(connection2);
        connectionConfiguration.insert(connection3);

        assertEquals(3, connectionConfiguration.getAll().size());

        connectionConfiguration.delete(connection2.getMetadataKey());

        assertEquals(2, connectionConfiguration.getAll().size());
        assertEquals(4, connectionParameterConfiguration.getAll().size());
    }

    @Test
    void connectionDeleteMultiple3Test() {
        connectionConfiguration.insert(connection1);
        connectionConfiguration.insert(connection2);
        connectionConfiguration.insert(connection3);

        assertEquals(3, connectionConfiguration.getAll().size());

        connectionConfiguration.delete(connection3.getMetadataKey());

        assertEquals(2, connectionConfiguration.getAll().size());
        assertEquals(4, connectionParameterConfiguration.getAll().size());
    }

    @Test
    void connectionDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () -> connectionConfiguration.delete(connection1.getMetadataKey()));
    }


    @Test
    void connectionGetNotExistsTest() {
        assertFalse(connectionConfiguration.exists(connection1));
        assertFalse(connectionConfiguration.get(connection1.getMetadataKey()).isPresent());
    }

    @Test
    void connectionUpdate1Test() {
        connectionConfiguration.insert(connection1);
        connectionConfiguration.insert(connection2);
        connectionConfiguration.insert(connection3);

        Optional<Connection> fetchedConnection1 = connectionConfiguration.get(connection1.getMetadataKey());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals("desc", fetchedConnection1.get().getDescription());
        Optional<Connection> fetchedConnection2 = connectionConfiguration.get(connection2.getMetadataKey());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals("desc", fetchedConnection2.get().getDescription());
        Optional<Connection> fetchedConnection3 = connectionConfiguration.get(connection3.getMetadataKey());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals("desc", fetchedConnection3.get().getDescription());

        connection1.setDescription("new desc");
        connectionConfiguration.update(connection1);

        fetchedConnection1 = connectionConfiguration.get(connection1.getMetadataKey());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals("new desc", fetchedConnection1.get().getDescription());
        fetchedConnection2 = connectionConfiguration.get(connection2.getMetadataKey());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals("new desc", fetchedConnection2.get().getDescription());
        fetchedConnection3 = connectionConfiguration.get(connection3.getMetadataKey());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals("desc", fetchedConnection3.get().getDescription());
    }

    @Test
    void connectionUpdate2Test() {
        connectionConfiguration.insert(connection1);
        connectionConfiguration.insert(connection2);
        connectionConfiguration.insert(connection3);

        Optional<Connection> fetchedConnection1 = connectionConfiguration.get(connection1.getMetadataKey());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals("desc", fetchedConnection1.get().getDescription());
        Optional<Connection> fetchedConnection2 = connectionConfiguration.get(connection2.getMetadataKey());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals("desc", fetchedConnection2.get().getDescription());
        Optional<Connection> fetchedConnection3 = connectionConfiguration.get(connection3.getMetadataKey());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals("desc", fetchedConnection3.get().getDescription());

        connection3.setDescription("new desc");
        connectionConfiguration.update(connection3);

        fetchedConnection1 = connectionConfiguration.get(connection1.getMetadataKey());
        assertTrue(fetchedConnection1.isPresent());
        assertEquals("desc", fetchedConnection1.get().getDescription());
        fetchedConnection2 = connectionConfiguration.get(connection2.getMetadataKey());
        assertTrue(fetchedConnection2.isPresent());
        assertEquals("desc", fetchedConnection2.get().getDescription());
        fetchedConnection3 = connectionConfiguration.get(connection3.getMetadataKey());
        assertTrue(fetchedConnection3.isPresent());
        assertEquals("new desc", fetchedConnection3.get().getDescription());
    }

}