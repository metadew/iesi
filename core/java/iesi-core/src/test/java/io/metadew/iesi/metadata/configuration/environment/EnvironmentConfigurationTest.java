package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
import io.metadew.iesi.metadata.repository.ConnectivityMetadataRepository;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
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

public class EnvironmentConfigurationTest {

    EnvironmentParameter environmentParameter;
    Environment environment;
    EnvironmentKey environmentKey;
    ConnectivityMetadataRepository connectivityMetadataRepository;

    @Before
    public void setup() {
        this.connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        EnvironmentParameterKey environmentParameterKey = new EnvironmentParameterKey("1", "firstParameter");
        environmentParameter = new EnvironmentParameter(environmentParameterKey, "parameter value");
        List<EnvironmentParameter> environmentParameters = new ArrayList<>();
        environmentParameters.add(environmentParameter);
        environmentKey = new EnvironmentKey("environment");
        environment = new Environment(environmentKey, "environment for testing", environmentParameters);
        try{
            EnvironmentConfiguration.getInstance().insert(environment);
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
    public void environmentNotExistsTest() {
        EnvironmentKey nonExistEnvironmentKey = new EnvironmentKey("non_exist");
        assertFalse(EnvironmentConfiguration.getInstance().exists(nonExistEnvironmentKey));
    }

    @Test
    public void environmentParameterExistsTest(){
        assertTrue(EnvironmentParameterConfiguration.getInstance().exists(environmentParameter.getMetadataKey()));
    }

    @Test
    public void environmentExistsTest(){
        assertTrue(EnvironmentConfiguration.getInstance().exists(environment.getMetadataKey()));
    }

    @Test
    public void environmentInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = EnvironmentConfiguration.getInstance().getAll().size();
        Environment newEnvironment = createEnvironment();
        EnvironmentConfiguration.getInstance().insert(newEnvironment);
        int nbAfter = EnvironmentConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void environmentInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> EnvironmentConfiguration.getInstance().insert(environment));
    }

    @Test
    public void environmentDeleteTest() throws MetadataDoesNotExistException {
        EnvironmentConfiguration.getInstance().delete(environment.getMetadataKey());
    }

    @Test
    public void environmentDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        Environment deleteScript = createEnvironment();
        assertThrows(MetadataDoesNotExistException.class,() -> EnvironmentConfiguration.getInstance().delete(deleteScript.getMetadataKey()));
    }

    @Test
    public void environmentGetTest() {
        Optional<Environment> newEnvironment = EnvironmentConfiguration.getInstance().get(environment.getMetadataKey());
        assertTrue(newEnvironment.isPresent());
        assertEquals(environment.getMetadataKey().getName(), newEnvironment.get().getMetadataKey().getName());
        assertEquals(environment.getDescription(), newEnvironment.get().getDescription());
    }

    @Test
    public void environmentGetNotExistsTest(){
        EnvironmentKey environmentParameterKey = new EnvironmentKey("not exist");
        assertFalse(EnvironmentConfiguration.getInstance().exists(environmentParameterKey));
        assertFalse(EnvironmentConfiguration.getInstance().get(environmentParameterKey).isPresent());
    }

    @Test
    public void environmentUpdateTest() throws MetadataDoesNotExistException {
        Environment environmentUpdate = environment;
        String newDescription = "new description";
        environmentUpdate.setDescription(newDescription);
        EnvironmentConfiguration.getInstance().update(environmentUpdate);
        Optional<Environment> checkScript = EnvironmentConfiguration.getInstance().get(environmentUpdate.getMetadataKey());
        assertTrue(checkScript.isPresent() && checkScript.get().getDescription().equals(newDescription));
    }

    private Environment createEnvironment(){
        EnvironmentKey newEnvironmentKey = new EnvironmentKey("new environmentkey");
        return new Environment(newEnvironmentKey, "created environment", new ArrayList<>());
    }
}
