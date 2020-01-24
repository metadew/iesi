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

    private DesignMetadataRepository designMetadataRepository;
    private ComponentParameter componentParameter11;
    private ComponentParameter componentParameter12;
    private ComponentParameter componentParameter2;
    private ComponentParameter componentParameter3;


    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        componentParameter11 = new ComponentParameterBuilder("1", 1, "parameter name 1")
                .value("value")
                .build();
        componentParameter12 = new ComponentParameterBuilder("1", 1, "parameter name 2")
                .value("value")
                .build();
        componentParameter2 = new ComponentParameterBuilder("1", 2, "parameter name 1")
                .value("value")
                .build();
        componentParameter3 = new ComponentParameterBuilder("2", 1, "parameter name 1")
                .value("value")
                .build();
    }

    @After
    public void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    public void componentParameterNotExistsTest() {
        assertFalse(ComponentParameterConfiguration.getInstance().exists(nonExistComponentParameterKey));
    }

    @Test
    public void componentParameterExistsTest(){
        assertTrue(ComponentParameterConfiguration.getInstance().exists(componentParameter11.getMetadataKey()));
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
        assertThrows(MetadataAlreadyExistsException.class,() -> ComponentParameterConfiguration.getInstance().insert(componentParameter11));
    }

    @Test
    public void componentParameterDeleteTest() throws MetadataDoesNotExistException {
        ComponentParameterConfiguration.getInstance().delete(componentParameter11.getMetadataKey());
    }

    @Test
    public void componentParameterDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ComponentParameter deleteComponentParameter = createComponentParameter();
        assertThrows(MetadataDoesNotExistException.class,() ->
                ComponentParameterConfiguration.getInstance().delete(deleteComponentParameter.getMetadataKey()));
    }

    @Test
    public void componentParameterGetTest() {
        Optional<ComponentParameter> newComponentParameter = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(newComponentParameter.isPresent());
        assertEquals(componentParameter11.getMetadataKey().getComponentId(),
                newComponentParameter.get().getMetadataKey().getComponentId());
        assertEquals(componentParameter11.getMetadataKey().getComponentVersionNb(),
                newComponentParameter.get().getMetadataKey().getComponentVersionNb());
        assertEquals(componentParameter11.getMetadataKey().getParameterName(),
                newComponentParameter.get().getMetadataKey().getParameterName());
        assertEquals(componentParameter11.getValue(), newComponentParameter.get().getValue());
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
        ComponentParameter componentParameterUpdate = componentParameter11;
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
