package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = { ConnectionParameterConfiguration.class  })
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ConnectionParameterConfigurationTest {

    private ConnectionParameter connectionParameter11;
    private ConnectivityMetadataRepository connectivityMetadataRepository;
    private ConnectionParameter connectionParameter12;
    private ConnectionParameter connectionParameter2;
    private ConnectionParameter connectionParameter3;

    @Autowired
    private ConnectionParameterConfiguration connectionParameterConfiguration;

    @BeforeEach
    void setup() {
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

    @Test
    void connectionParameterNotExistsTest() {
        assertFalse(connectionParameterConfiguration.exists(connectionParameter11));
    }

    @Test
    void connectionParameterExistsTest() {
        connectionParameterConfiguration.insert(connectionParameter11);
        assertTrue(connectionParameterConfiguration.exists(connectionParameter11.getMetadataKey()));
    }

    @Test
    void connectionParameterInsertTest() {
        assertEquals(0, connectionParameterConfiguration.getAll().size());

        connectionParameterConfiguration.insert(connectionParameter11);

        assertEquals(1, connectionParameterConfiguration.getAll().size());

        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter11, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterInsertAlreadyExistsTest() {
        connectionParameterConfiguration.insert(connectionParameter11);
        assertThrows(MetadataAlreadyExistsException.class,() -> connectionParameterConfiguration.insert(connectionParameter11));
    }

    @Test
    void connectionParameterDeleteOnlyTest() {
        connectionParameterConfiguration.insert(connectionParameter11);
        assertEquals(1, connectionParameterConfiguration.getAll().size());

        connectionParameterConfiguration.delete(connectionParameter11.getMetadataKey());

        assertEquals(0, connectionParameterConfiguration.getAll().size());
    }

    @Test
    void connectionParameterDeleteMultiplePerConnectionEnvTest() {
        connectionParameterConfiguration.insert(connectionParameter11);
        connectionParameterConfiguration.insert(connectionParameter12);
        assertEquals(2, connectionParameterConfiguration.getAll().size());

        connectionParameterConfiguration.delete(connectionParameter11.getMetadataKey());

        assertEquals(1, connectionParameterConfiguration.getAll().size());
        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter12.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter12, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterDeleteMultiplePerConnectionTest() {
        connectionParameterConfiguration.insert(connectionParameter2);
        connectionParameterConfiguration.insert(connectionParameter3);
        assertEquals(2, connectionParameterConfiguration.getAll().size());

        connectionParameterConfiguration.delete(connectionParameter2.getMetadataKey());

        assertEquals(1, connectionParameterConfiguration.getAll().size());
        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter3.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter3, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() -> connectionParameterConfiguration.delete(connectionParameter11.getMetadataKey()));
    }

    @Test
    void connectionParameterGetOnlyTest() {
        connectionParameterConfiguration.insert(connectionParameter11);

        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter11, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterGetMultiplePerConnectionEnvTest() {
        connectionParameterConfiguration.insert(connectionParameter11);
        connectionParameterConfiguration.insert(connectionParameter12);

        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter11, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterGetMultiplePerConnectionTest() {
        connectionParameterConfiguration.insert(connectionParameter2);
        connectionParameterConfiguration.insert(connectionParameter3);

        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter2.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals(connectionParameter2, fetchedConnectionParameter.get());
    }

    @Test
    void connectionParameterGetNotExistsTest(){
        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter2.getMetadataKey());
        assertFalse(fetchedConnectionParameter.isPresent());
    }

    @Test
    void connectionParameterUpdateSingleTest() {
        connectionParameterConfiguration.insert(connectionParameter11);

        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("parameter value", fetchedConnectionParameter.get().getValue());

        connectionParameter11.setValue("dummy");
        connectionParameterConfiguration.update(connectionParameter11);


        fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("dummy", fetchedConnectionParameter.get().getValue());
    }

    @Test
    void connectionParameterUpdateMultiplePerConnectionEnvTest() {
        connectionParameterConfiguration.insert(connectionParameter11);
        connectionParameterConfiguration.insert(connectionParameter12);

        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("parameter value", fetchedConnectionParameter.get().getValue());

        connectionParameter11.setValue("dummy");
        connectionParameterConfiguration.update(connectionParameter11);


        fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter11.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("dummy", fetchedConnectionParameter.get().getValue());
    }

    @Test
    void connectionParameterUpdateMultiplePerConnectionTest() {
        connectionParameterConfiguration.insert(connectionParameter2);
        connectionParameterConfiguration.insert(connectionParameter3);

        Optional<ConnectionParameter> fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter2.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("parameter value", fetchedConnectionParameter.get().getValue());

        connectionParameter2.setValue("dummy");
        connectionParameterConfiguration.update(connectionParameter2);


        fetchedConnectionParameter = connectionParameterConfiguration.get(connectionParameter2.getMetadataKey());
        assertTrue(fetchedConnectionParameter.isPresent());
        assertEquals("dummy", fetchedConnectionParameter.get().getValue());
    }

}
