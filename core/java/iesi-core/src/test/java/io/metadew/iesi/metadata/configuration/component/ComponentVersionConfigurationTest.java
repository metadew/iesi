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

public class ComponentVersionConfigurationTest {

    DesignMetadataRepository designMetadataRepository;
    ComponentVersion componentVersion;


    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        String componentId = "1";
        long versionNb = 1;
        componentVersion = new ComponentVersion(new ComponentVersionKey("1", 1),
                "version of component");
        try{
            ComponentVersionConfiguration.getInstance().insert(componentVersion);
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
    public void componentVersionNotExistsTest() {
        ComponentVersionKey nonExistComponentVersionKey = new ComponentVersionKey("non_exist", 1);
        assertFalse(ComponentVersionConfiguration.getInstance().exists(nonExistComponentVersionKey));
    }

    @Test
    public void componentVersionExistsTest(){
        assertTrue(ComponentVersionConfiguration.getInstance().exists(componentVersion.getMetadataKey()));
    }

    @Test
    public void componentVersionInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ComponentVersionConfiguration.getInstance().getAll().size();
        ComponentVersion newComponentVersion = createComponentVersion();
        ComponentVersionConfiguration.getInstance().insert(newComponentVersion);
        int nbAfter = ComponentVersionConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void componentVersionInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ComponentVersionConfiguration.getInstance().insert(componentVersion));
    }

    @Test
    public void componentVersionDeleteTest() throws MetadataDoesNotExistException {
        ComponentVersionConfiguration.getInstance().delete(componentVersion.getMetadataKey());
    }

    @Test
    public void componentVersionDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ComponentVersion deleteComponentVersion = createComponentVersion();
        assertThrows(MetadataDoesNotExistException.class,() ->
                ComponentVersionConfiguration.getInstance().delete(deleteComponentVersion.getMetadataKey()));
    }

    @Test
    public void componentVersionGetTest() {
        Optional<ComponentVersion> newComponentVersion = ComponentVersionConfiguration.getInstance().get(componentVersion.getMetadataKey());
        assertTrue(newComponentVersion.isPresent());
        assertEquals(componentVersion.getMetadataKey().getComponentId(),
                newComponentVersion.get().getMetadataKey().getComponentId());
        assertEquals(componentVersion.getMetadataKey().getComponentVersionNumber(),
                newComponentVersion.get().getMetadataKey().getComponentVersionNumber());
        assertEquals(componentVersion.getDescription(), newComponentVersion.get().getDescription());
    }

    @Test
    public void componentVersionGetNotExistsTest(){
        ComponentVersionKey componentVersionKey = new ComponentVersionKey("3", 1);
        assertFalse(ComponentVersionConfiguration.getInstance().exists(componentVersionKey));
        assertFalse(ComponentVersionConfiguration.getInstance().get(componentVersionKey).isPresent());
    }

    @Test
    public void componentVersionUpdateTest() throws MetadataDoesNotExistException {
        ComponentVersion componentVersionUpdate = componentVersion;
        String newDescription = "new description";
        componentVersionUpdate.setDescription(newDescription);
        ComponentVersionConfiguration.getInstance().update(componentVersionUpdate);
        Optional<ComponentVersion> checkComponentVersion = ComponentVersionConfiguration.getInstance().get(componentVersionUpdate.getMetadataKey());
        assertTrue(checkComponentVersion.isPresent() && checkComponentVersion.get().getDescription().equals(newDescription));
    }

    private ComponentVersion createComponentVersion(){
        ComponentVersion version = new ComponentVersion(new ComponentVersionKey("new", 1),
                "version of component");
        return version;
    }
}
