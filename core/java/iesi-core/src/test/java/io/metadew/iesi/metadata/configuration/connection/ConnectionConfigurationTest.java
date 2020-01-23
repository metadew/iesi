package io.metadew.iesi.metadata.configuration.connection;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConnectionConfigurationTest {

    ConnectionParameter connectionParameter;
    Connection connection;
    ConnectionKey connectionKey;
    ConnectivityMetadataRepository connectivityMetadataRepository;

    @Before
    public void setup() {
        connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        ConnectionParameterKey connectionParameterKey = new ConnectionParameterKey("connection",
                "test", "connection parameter");
        connectionParameter = new ConnectionParameter(connectionParameterKey, "parameter value");
        List<ConnectionParameter> connectionParameters = new ArrayList<>();
        connectionParameters.add(connectionParameter);
        connectionKey = new ConnectionKey("connection", "test");
        connection = new Connection(connectionKey, "connection type", "connection used for testing",
                connectionParameters);
        try{
            ConnectionConfiguration.getInstance().insert(connection);
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
    public void connectionNotExistsTest() {
        ConnectionKey nonExistConnectionKey = new ConnectionKey("non_existing connection", "test");
        assertFalse(ConnectionConfiguration.getInstance().exists(nonExistConnectionKey));
    }

    @Test
    public void connectionParameterExistsTest(){
        assertTrue(ConnectionParameterConfiguration.getInstance().exists(connectionParameter.getMetadataKey()));
    }

    @Test
    public void connectionExistsTest(){
        assertTrue(ConnectionConfiguration.getInstance().exists(connection.getMetadataKey()));
    }

    @Test
    public void connectionInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ConnectionConfiguration.getInstance().getAll().size();
        Connection newConnection = createConnection();
        ConnectionConfiguration.getInstance().insert(newConnection);
        int nbAfter = ConnectionConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void connectionInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ConnectionConfiguration.getInstance().insert(connection));
    }

    @Test
    public void connectionDeleteTest() throws MetadataDoesNotExistException {
        ConnectionConfiguration.getInstance().delete(connection.getMetadataKey());
    }

    @Test
    public void connectionDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        Connection deleteScript = createConnection();
        assertThrows(MetadataDoesNotExistException.class,() -> ConnectionConfiguration.getInstance().delete(deleteScript.getMetadataKey()));
    }

    @Test
    public void connectionGetTest() {
        Optional<Connection> newConnection = ConnectionConfiguration.getInstance().get(connection.getMetadataKey());
        assertTrue(newConnection.isPresent());
        assertEquals(connection.getMetadataKey().getName(), newConnection.get().getMetadataKey().getName());
        assertEquals(connection.getDescription(), newConnection.get().getDescription());
    }

    @Test
    public void connectionGetNotExistsTest(){
        ConnectionKey connectionParameterKey = new ConnectionKey("not exist", "test");
        assertFalse(ConnectionConfiguration.getInstance().exists(connectionParameterKey));
        assertFalse(ConnectionConfiguration.getInstance().get(connectionParameterKey).isPresent());
    }

    @Test
    public void connectionUpdateTest() throws MetadataDoesNotExistException {
        Connection connectionUpdate = connection;
        String newDescription = "new description";
        connectionUpdate.setDescription(newDescription);
        ConnectionConfiguration.getInstance().update(connectionUpdate);
        Optional<Connection> checkScript = ConnectionConfiguration.getInstance().get(connectionUpdate.getMetadataKey());
        assertTrue(checkScript.isPresent() && checkScript.get().getDescription().equals(newDescription));
    }

    private Connection createConnection(){
        ConnectionKey newConnectionKey = new ConnectionKey("new connectionkey", "test");
        return new Connection(newConnectionKey, "connection type", "created connection",
                new ArrayList<>());
    }
}
