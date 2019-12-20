package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.configuration.exception.ComponentAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
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

public class ComponentConfigurationTest {

    DesignMetadataRepository designMetadataRepository;
    Component component;
    ComponentVersion componentVersion;
    ComponentParameter componentParameter;
    ComponentParameterKey componentParameterKey;
    ComponentAttribute componentAttribute;
    ComponentAttributeKey componentAttributeKey;


    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        String componentId = "1";
        long versionNb = 1;
        String componentParameterName = "component parameter";
        String attributeName = "component attribute";
        componentParameterKey = new ComponentParameterKey(componentId, versionNb, componentParameterName);
        componentParameter = new ComponentParameter(componentParameterKey, "parameter value");
        List<ComponentParameter> componentParameters = new ArrayList<>();
        componentParameters.add(componentParameter);
        componentAttributeKey = new ComponentAttributeKey(componentId, versionNb, attributeName);
        componentAttribute = new ComponentAttribute(componentAttributeKey, "environment", "component attribute");
        List<ComponentAttribute> componentAttributes = new ArrayList<>();
        componentAttributes.add(componentAttribute);
        componentVersion = new ComponentVersion(new ComponentVersionKey("1", 1),
                "version of component");
        component = new Component(new ComponentKey("1", 1), "component", "testComponentExist",
                "component for testing", componentVersion,
                componentParameters, componentAttributes);
        try{
            ComponentConfiguration.getInstance().insert(component);
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
    public void componentNotExistsTest() {
        ComponentKey nonExistComponentKey = new ComponentKey("non_exist", 1);
        assertFalse(ComponentConfiguration.getInstance().exists(nonExistComponentKey));
    }

    @Test
    public void componentParameterExistsTest(){
        assertTrue(ComponentParameterConfiguration.getInstance().exists(componentParameter.getMetadataKey()));
    }

    @Test
    public void componentExistsTest(){
        assertTrue(ComponentConfiguration.getInstance().exists(component.getMetadataKey()));
    }

    @Test
    public void componentInsertTest() throws ComponentAlreadyExistsException {
        int nbBefore = ComponentConfiguration.getInstance().getAll().size();
        Component newComponent = createComponent();
        ComponentConfiguration.getInstance().insert(newComponent);
        int nbAfter = ComponentConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void componentInsertAlreadyExistsTest() {
        assertThrows(ComponentAlreadyExistsException.class,() -> ComponentConfiguration.getInstance().insert(component));
    }

    @Test
    public void componentDeleteTest() throws MetadataDoesNotExistException {
        ComponentConfiguration.getInstance().delete(component.getMetadataKey());
    }

    @Test
    public void componentDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        Component deleteComponent = createComponent();
        assertThrows(MetadataDoesNotExistException.class,() -> ComponentConfiguration.getInstance().delete(deleteComponent.getMetadataKey()));
    }

    @Test
    public void componentGetTest() {
        Optional<Component> newComponent = ComponentConfiguration.getInstance().get(component.getMetadataKey());
        assertTrue(newComponent.isPresent());
        assertEquals(component.getMetadataKey().getId(), newComponent.get().getMetadataKey().getId());;
    }

    @Test
    public void componentGetNotExistsTest(){
        ComponentKey componentKey = new ComponentKey("3", 1);
        assertFalse(ComponentConfiguration.getInstance().exists(componentKey));
        assertFalse(ComponentConfiguration.getInstance().get(componentKey).isPresent());
    }

    @Test
    public void componentUpdateTest() throws MetadataDoesNotExistException {
        Component componentUpdate = component;
        String newDescription = "new description";
        componentUpdate.setDescription(newDescription);
        ComponentConfiguration.getInstance().update(componentUpdate);
        Optional<Component> checkComponent = ComponentConfiguration.getInstance().get(componentUpdate.getMetadataKey());
        assertTrue(checkComponent.isPresent() && checkComponent.get().getDescription().equals(newDescription));
    }

    private Component createComponent(){
        String componentId = "new id";
        long versionNb = 5;
        String componentParameterName = "new parameter name";
        String attributeName = "new attribute name";
        ComponentParameterKey parameterKey = new ComponentParameterKey(componentId, versionNb, componentParameterName);
        ComponentParameter parameter = new ComponentParameter(parameterKey, "parameter value");
        List<ComponentParameter> parameters = new ArrayList<>();
        parameters.add(parameter);
        ComponentAttributeKey attributeKey = new ComponentAttributeKey(componentId, versionNb, attributeName);
        ComponentAttribute attribute = new ComponentAttribute(attributeKey, "environment", "component attribute");
        List<ComponentAttribute> attributes = new ArrayList<>();
        attributes.add(attribute);
        ComponentVersion version = new ComponentVersion(new ComponentVersionKey("1", 1),
                "version of component");
        Component newComponent = new Component(new ComponentKey(componentId, 1), "component", "new component",
                "component for testing", version,
                parameters, attributes);
        return newComponent;
    }
}
