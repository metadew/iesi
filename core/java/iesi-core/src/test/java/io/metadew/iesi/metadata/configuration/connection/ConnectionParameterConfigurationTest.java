package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConnectionParameterConfigurationTest {

    ConnectionParameter connectionParameter;
    ConnectionParameterKey connectionParameterKey;
    ConnectivityMetadataRepository connectivityMetadataRepository;

    @Before
    public void setup() {
        this.connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        connectionParameterKey = new ConnectionParameterKey("connectionParameter", "test", "parameter name");
        connectionParameter = new ConnectionParameter(connectionParameterKey,  "parameter value");
        try{
            ConnectionParameterConfiguration.getInstance().insert(connectionParameter);
        }catch(MetadataAlreadyExistsException ignored){
            // if script already is in database do nothing
            System.out.println("something went wrong");
        }


    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        connectivityMetadataRepository.dropAllTables();
    }

    @Test
    public void connectionParameterNotExistsTest() {
        ConnectionParameterKey nonExistConnectionParameterKey = new ConnectionParameterKey("non_exist",
                "test", "non exist par name");
        assertFalse(ConnectionParameterConfiguration.getInstance().exists(nonExistConnectionParameterKey));
    }

    @Test
    public void connectionParameterExistsTest(){
        assertTrue(ConnectionParameterConfiguration.getInstance().exists(connectionParameter.getMetadataKey()));
    }

    @Test
    public void connectionParameterInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ConnectionParameterConfiguration.getInstance().getAll().size();
        ConnectionParameter newConnectionParameter = createConnectionParameter();
        ConnectionParameterConfiguration.getInstance().insert(newConnectionParameter);
        int nbAfter = ConnectionParameterConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void connectionParameterInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ConnectionParameterConfiguration.getInstance().insert(connectionParameter));
    }

    @Test
    public void connectionParameterDeleteTest() throws MetadataDoesNotExistException {
        ConnectionParameterConfiguration.getInstance().delete(connectionParameter.getMetadataKey());
    }

    @Test
    public void connectionParameterDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ConnectionParameter deleteScript = createConnectionParameter();
        assertThrows(MetadataDoesNotExistException.class,() -> ConnectionParameterConfiguration.getInstance().delete(deleteScript.getMetadataKey()));
    }

    @Test
    public void connectionParameterGetTest() {
        Optional<ConnectionParameter> newConnectionParameter = ConnectionParameterConfiguration.getInstance().get(connectionParameter.getMetadataKey());
        assertTrue(newConnectionParameter.isPresent());
        assertEquals(connectionParameter.getMetadataKey().getConnectionName(), newConnectionParameter.get().getMetadataKey().getConnectionName());
        assertEquals(connectionParameter.getValue(), newConnectionParameter.get().getValue());
    }

    @Test
    public void connectionParameterGetNotExistsTest(){
        ConnectionParameterKey connectionParameterParameterKey = new ConnectionParameterKey("not exist",
                "test", "not exist par name");
        assertFalse(ConnectionParameterConfiguration.getInstance().exists(connectionParameterParameterKey));
        assertFalse(ConnectionParameterConfiguration.getInstance().get(connectionParameterParameterKey).isPresent());
    }

    @Test
    public void connectionParameterUpdateTest() throws MetadataDoesNotExistException {
        ConnectionParameter connectionParameterUpdate = connectionParameter;
        String newValue = "new value";
        connectionParameterUpdate.setValue(newValue);
        ConnectionParameterConfiguration.getInstance().update(connectionParameterUpdate);
        Optional<ConnectionParameter> checkScript = ConnectionParameterConfiguration.getInstance().get(connectionParameterUpdate.getMetadataKey());
        assertTrue(checkScript.isPresent() && checkScript.get().getValue().equals(newValue));
    }

    private ConnectionParameter createConnectionParameter(){
        ConnectionParameterKey newConnectionParameterKey = new ConnectionParameterKey("new connectionParameterkey",
                "test", "new par name");
        return new ConnectionParameter(newConnectionParameterKey, "new par value");
    }
}
