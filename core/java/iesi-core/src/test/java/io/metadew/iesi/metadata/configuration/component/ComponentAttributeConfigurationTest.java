package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.key.ComponentAttributeKey;
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

public class ComponentAttributeConfigurationTest {

    DesignMetadataRepository designMetadataRepository;
    ComponentAttribute componentAttribute;

    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        String componentId = "1";
        long attributeNb = 1;
        componentAttribute = new ComponentAttribute(new ComponentAttributeKey("1", 1,
                "attribute name"),
                "test",
                "attribute of component");
        try{
            ComponentAttributeConfiguration.getInstance().insert(componentAttribute);
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
    public void componentAttributeNotExistsTest() {
        ComponentAttributeKey nonExistComponentAttributeKey = new ComponentAttributeKey("non_exist",
                1, "non exist");
        assertFalse(ComponentAttributeConfiguration.getInstance().exists(nonExistComponentAttributeKey));
    }

    @Test
    public void componentAttributeExistsTest(){
        assertTrue(ComponentAttributeConfiguration.getInstance().exists(componentAttribute.getMetadataKey()));
    }

    @Test
    public void componentAttributeInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ComponentAttributeConfiguration.getInstance().getAll().size();
        ComponentAttribute newComponentAttribute = createComponentAttribute();
        ComponentAttributeConfiguration.getInstance().insert(newComponentAttribute);
        int nbAfter = ComponentAttributeConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void componentAttributeInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ComponentAttributeConfiguration.getInstance().insert(componentAttribute));
    }

    @Test
    public void componentAttributeDeleteTest() throws MetadataDoesNotExistException {
        ComponentAttributeConfiguration.getInstance().delete(componentAttribute.getMetadataKey());
    }

    @Test
    public void componentAttributeDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ComponentAttribute deleteComponentAttribute = createComponentAttribute();
        assertThrows(MetadataDoesNotExistException.class,() ->
                ComponentAttributeConfiguration.getInstance().delete(deleteComponentAttribute.getMetadataKey()));
    }

    @Test
    public void componentAttributeGetTest() {
        Optional<ComponentAttribute> newComponentAttribute = ComponentAttributeConfiguration.getInstance().get(componentAttribute.getMetadataKey());
        assertTrue(newComponentAttribute.isPresent());
        assertEquals(componentAttribute.getMetadataKey().getComponentId(),
                newComponentAttribute.get().getMetadataKey().getComponentId());
        assertEquals(componentAttribute.getMetadataKey().getComponentVersionNb(),
                newComponentAttribute.get().getMetadataKey().getComponentVersionNb());
        assertEquals(componentAttribute.getMetadataKey().getComponentAttributeName(),
                newComponentAttribute.get().getMetadataKey().getComponentAttributeName());
        assertEquals(componentAttribute.getValue(), newComponentAttribute.get().getValue());
    }

    @Test
    public void componentAttributeGetNotExistsTest(){
        ComponentAttributeKey componentAttributeKey = new ComponentAttributeKey("3", 1,
                "attribute name not exist");
        assertFalse(ComponentAttributeConfiguration.getInstance().exists(componentAttributeKey));
        assertFalse(ComponentAttributeConfiguration.getInstance().get(componentAttributeKey).isPresent());
    }

    @Test
    public void componentAttributeUpdateTest() throws MetadataDoesNotExistException {
        ComponentAttribute componentAttributeUpdate = componentAttribute;
        String newValue = "new value";
        componentAttributeUpdate.setValue(newValue);
        ComponentAttributeConfiguration.getInstance().update(componentAttributeUpdate);
        Optional<ComponentAttribute> checkComponentAttribute = ComponentAttributeConfiguration.getInstance().get(componentAttributeUpdate.getMetadataKey());
        assertTrue(checkComponentAttribute.isPresent() && checkComponentAttribute.get().getValue().equals(newValue));
    }

    private ComponentAttribute createComponentAttribute(){
        ComponentAttribute attribute = new ComponentAttribute(new ComponentAttributeKey("new",
                1, "create attribute"),
                "test",
                "attribute of component");
        return attribute;
    }
}
