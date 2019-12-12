package io.metadew.iesi.metadata.configuration.environment;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.environment.EnvironmentParameter;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;
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

public class EnvironmentParameterConfigurationTest {
    
    EnvironmentParameter environmentParameter;
    EnvironmentParameterKey environmentParameterKey;
    ConnectivityMetadataRepository connectivityMetadataRepository;

    @Before
    public void setup() {
        this.connectivityMetadataRepository = RepositoryTestSetup.getConnectivityMetadataRepository();
        environmentParameterKey = new EnvironmentParameterKey("environmentParameter", "parameter name");
        environmentParameter = new EnvironmentParameter(environmentParameterKey,  "parameter value");
        try{
            EnvironmentParameterConfiguration.getInstance().insert(environmentParameter);
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
    public void environmentParameterNotExistsTest() {
        EnvironmentParameterKey nonExistEnvironmentParameterKey = new EnvironmentParameterKey("non_exist",
                "non exist par name");
        assertFalse(EnvironmentParameterConfiguration.getInstance().exists(nonExistEnvironmentParameterKey));
    }

    @Test
    public void environmentParameterExistsTest(){
        assertTrue(EnvironmentParameterConfiguration.getInstance().exists(environmentParameter.getMetadataKey()));
    }

    @Test
    public void environmentParameterInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = EnvironmentParameterConfiguration.getInstance().getAll().size();
        EnvironmentParameter newEnvironmentParameter = createEnvironmentParameter();
        EnvironmentParameterConfiguration.getInstance().insert(newEnvironmentParameter);
        int nbAfter = EnvironmentParameterConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void environmentParameterInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> EnvironmentParameterConfiguration.getInstance().insert(environmentParameter));
    }

    @Test
    public void environmentParameterDeleteTest() throws MetadataDoesNotExistException {
        EnvironmentParameterConfiguration.getInstance().delete(environmentParameter.getMetadataKey());
    }

    @Test
    public void environmentParameterDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        EnvironmentParameter deleteScript = createEnvironmentParameter();
        assertThrows(MetadataDoesNotExistException.class,() -> EnvironmentParameterConfiguration.getInstance().delete(deleteScript.getMetadataKey()));
    }

    @Test
    public void environmentParameterGetTest() {
        Optional<EnvironmentParameter> newEnvironmentParameter = EnvironmentParameterConfiguration.getInstance().get(environmentParameter.getMetadataKey());
        assertTrue(newEnvironmentParameter.isPresent());
        assertEquals(environmentParameter.getMetadataKey().getEnvironmentName(), newEnvironmentParameter.get().getMetadataKey().getEnvironmentName());
        assertEquals(environmentParameter.getValue(), newEnvironmentParameter.get().getValue());
    }

    @Test
    public void environmentParameterGetNotExistsTest(){
        EnvironmentParameterKey environmentParameterParameterKey = new EnvironmentParameterKey("not exist",
                "not exist par name");
        assertFalse(EnvironmentParameterConfiguration.getInstance().exists(environmentParameterParameterKey));
        assertFalse(EnvironmentParameterConfiguration.getInstance().get(environmentParameterParameterKey).isPresent());
    }

    @Test
    public void environmentParameterUpdateTest() throws MetadataDoesNotExistException {
        EnvironmentParameter environmentParameterUpdate = environmentParameter;
        String newValue = "new value";
        environmentParameterUpdate.setValue(newValue);
        EnvironmentParameterConfiguration.getInstance().update(environmentParameterUpdate);
        Optional<EnvironmentParameter> checkScript = EnvironmentParameterConfiguration.getInstance().get(environmentParameterUpdate.getMetadataKey());
        assertTrue(checkScript.isPresent() && checkScript.get().getValue().equals(newValue));
    }

    private EnvironmentParameter createEnvironmentParameter(){
        EnvironmentParameterKey newEnvironmentParameterKey = new EnvironmentParameterKey("new environmentParameterkey",
                 "new par name");
        return new EnvironmentParameter(newEnvironmentParameterKey, "new par value");
    }
}
