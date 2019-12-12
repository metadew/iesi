package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComponentParameterConfigurationTest {

    DesignMetadataRepository designMetadataRepository;
    ComponentParameter componentParameter;


    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        String componentId = "1";
        long parameterNb = 1;
        componentParameter = new ComponentParameter(new ComponentParameterKey("1", 1,
                "parameter name"),
                "parameter of component");
        try{
            ComponentParameterConfiguration.getInstance().insert(componentParameter);
        }catch(MetadataAlreadyExistsException ignored){
            // if component already is in database do nothing
        }
    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    public void componentParameterNotExistsTest() {
        ComponentParameterKey nonExistComponentParameterKey = new ComponentParameterKey("non_exist",
                1, "non exist");
        assertFalse(ComponentParameterConfiguration.getInstance().exists(nonExistComponentParameterKey));
    }

    @Test
    public void componentParameterExistsTest(){
        assertTrue(ComponentParameterConfiguration.getInstance().exists(componentParameter.getMetadataKey()));
    }

    @Test
    public void componentParameterInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ComponentParameterConfiguration.getInstance().getAll().size();
        ComponentParameter newComponentParameter = createComponentParameter();
        ComponentParameterConfiguration.getInstance().insert(newComponentParameter);
        int nbAfter = ComponentParameterConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void componentParameterInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ComponentParameterConfiguration.getInstance().insert(componentParameter));
    }

    @Test
    public void componentParameterDeleteTest() throws MetadataDoesNotExistException {
        ComponentParameterConfiguration.getInstance().delete(componentParameter.getMetadataKey());
    }

    @Test
    public void componentParameterDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ComponentParameter deleteComponentParameter = createComponentParameter();
        assertThrows(MetadataDoesNotExistException.class,() ->
                ComponentParameterConfiguration.getInstance().delete(deleteComponentParameter.getMetadataKey()));
    }

    @Test
    public void componentParameterGetTest() {
        Optional<ComponentParameter> newComponentParameter = ComponentParameterConfiguration.getInstance().get(componentParameter.getMetadataKey());
        assertTrue(newComponentParameter.isPresent());
        assertEquals(componentParameter.getMetadataKey().getComponentId(),
                newComponentParameter.get().getMetadataKey().getComponentId());
        assertEquals(componentParameter.getMetadataKey().getComponentVersionNb(),
                newComponentParameter.get().getMetadataKey().getComponentVersionNb());
        assertEquals(componentParameter.getMetadataKey().getComponentParameterName(),
                newComponentParameter.get().getMetadataKey().getComponentParameterName());
        assertEquals(componentParameter.getValue(), newComponentParameter.get().getValue());
    }

    @Test
    public void componentParameterGetNotExistsTest(){
        ComponentParameterKey componentParameterKey = new ComponentParameterKey("3", 1,
                "parameter name not exist");
        assertFalse(ComponentParameterConfiguration.getInstance().exists(componentParameterKey));
        assertFalse(ComponentParameterConfiguration.getInstance().get(componentParameterKey).isPresent());
    }

    @Test
    public void componentParameterUpdateTest() throws MetadataDoesNotExistException {
        ComponentParameter componentParameterUpdate = componentParameter;
        String newValue = "new value";
        componentParameterUpdate.setValue(newValue);
        ComponentParameterConfiguration.getInstance().update(componentParameterUpdate);
        Optional<ComponentParameter> checkComponentParameter = ComponentParameterConfiguration.getInstance().get(componentParameterUpdate.getMetadataKey());
        assertTrue(checkComponentParameter.isPresent() && checkComponentParameter.get().getValue().equals(newValue));
    }

    private ComponentParameter createComponentParameter(){
        ComponentParameter parameter = new ComponentParameter(new ComponentParameterKey("new",
                1, "create parameter"),
                "parameter of component");
        return parameter;
    }
}
