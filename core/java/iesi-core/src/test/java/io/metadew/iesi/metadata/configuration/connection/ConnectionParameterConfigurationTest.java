package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionParameterConfigurationTest {

    private ConnectionParameter connectionParameter11;
    private ConnectivityMetadataRepository connectivityMetadataRepository;
    private ConnectionParameter connectionParameter12;
    private ConnectionParameter connectionParameter2;
    private ConnectionParameter connectionParameter3;

    @BeforeEach
    void setup() {
        this.connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        connectivityMetadataRepository.createAllTables();
        connectionParameter11 = new ConnectionParameterBuilder("connection1", "env1", "parameter name 1")
                .value("parameter value")
                .build();
        connectionParameter12 = new ConnectionParameterBuilder("connection1", "env1", "parameter name 2")
                .value("parameter value")
                .build();
        connectionParameter2 = new ConnectionParameterBuilder("connection2", "env1", "parameter name 1")
                .value("parameter value")
                .build();
        connectionParameter3 = new ConnectionParameterBuilder("connection2", "env2", "parameter name 1")
                .value("parameter value")
                .build();
    }

    @AfterEach
    void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        connectivityMetadataRepository.dropAllTables();
    }

    @Test
    void connectionParameterNotExistsTest() {
        assertFalse(ConnectionParameterConfiguration.getInstance().exists(connectionParameter11));
    }

    @Test
    void connectionParameterExistsTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter11);
        assertTrue(ConnectionParameterConfiguration.getInstance().exists(connectionParameter11.getMetadataKey()));
    }

    @Test
    void connectionParameterInsertTest() {
        assertEquals(0, ConnectionParameterConfiguration.getInstance().getAll().size());

        ConnectionParameterConfiguration.getInstance().insert(connectionParameter11);

        assertEquals(1, ConnectionParameterConfiguration.getInstance().getAll().size());

        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter11, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterInsertAlreadyExistsTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter11);
        assertThrows(MetadataAlreadyExistsException.class,() -> ConnectionParameterConfiguration.getInstance().insert(connectionParameter11));
    }

    @Test
    void connectionParameterDeleteOnlyTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter11);
        assertEquals(1, ConnectionParameterConfiguration.getInstance().getAll().size());

        ConnectionParameterConfiguration.getInstance().delete(connectionParameter11.getMetadataKey());

        assertEquals(0, ConnectionParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    void connectionParameterDeleteMultiplePerConnectionEnvTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter11);
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter12);
        assertEquals(2, ConnectionParameterConfiguration.getInstance().getAll().size());

        ConnectionParameterConfiguration.getInstance().delete(connectionParameter11.getMetadataKey());

        assertEquals(1, ConnectionParameterConfiguration.getInstance().getAll().size());
        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter12.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter12, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterDeleteMultiplePerConnectionTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter2);
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter3);
        assertEquals(2, ConnectionParameterConfiguration.getInstance().getAll().size());

        ConnectionParameterConfiguration.getInstance().delete(connectionParameter2.getMetadataKey());

        assertEquals(1, ConnectionParameterConfiguration.getInstance().getAll().size());
        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter3.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter3, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() -> ConnectionParameterConfiguration.getInstance().delete(connectionParameter11.getMetadataKey()));
    }

    @Test
    void connectionParameterGetOnlyTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter11);

        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter11, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterGetMultiplePerConnectionEnvTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter11);
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter12);

        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter11, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterGetMultiplePerConnectionTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter2);
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter3);

        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter2.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter2, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterGetNotExistsTest(){
        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter2.getMetadataKey());
        assertFalse(fetchedConnectionParameter.isPresent());
    }

    @Test
    void connectionParameterUpdateSingleTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter11);

        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("parameter value", fetchedConnectionParameter.get().getValue());

        connectionParameter11.setValue("dummy");
        ConnectionParameterConfiguration.getInstance().update(connectionParameter11);


        fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("dummy", fetchedConnectionParameter.get().getValue());
    }

    @Test
    void connectionParameterUpdateMultiplePerConnectionEnvTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter11);
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter12);

        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("parameter value", fetchedConnectionParameter.get().getValue());

        connectionParameter11.setValue("dummy");
        ConnectionParameterConfiguration.getInstance().update(connectionParameter11);


        fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("dummy", fetchedConnectionParameter.get().getValue());
    }

    @Test
    void connectionParameterUpdateMultiplePerConnectionTest() {
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter2);
        ConnectionParameterConfiguration.getInstance().insert(connectionParameter3);

        Optional<ConnectionParameter> fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter2.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("parameter value", fetchedConnectionParameter.get().getValue());

        connectionParameter2.setValue("dummy");
        ConnectionParameterConfiguration.getInstance().update(connectionParameter2);


        fetchedConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter2.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("dummy", fetchedConnectionParameter.get().getValue());
    }

}
