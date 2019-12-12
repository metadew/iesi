package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentBuild;
import io.metadew.iesi.metadata.definition.component.key.ComponentBuildKey;
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

public class ComponentBuildConfigurationTest {

    DesignMetadataRepository designMetadataRepository;
    ComponentBuild componentBuild;


    @Before
    public void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        String componentId = "1";
        long buildNb = 1;
        componentBuild = new ComponentBuild(new ComponentBuildKey("1", 1,
                "build name"),
                "build of component");
        try{
            ComponentBuildConfiguration.getInstance().insert(componentBuild);
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
    public void componentBuildNotExistsTest() {
        ComponentBuildKey nonExistComponentBuildKey = new ComponentBuildKey("non_exist",
                1, "non exist");
        assertFalse(ComponentBuildConfiguration.getInstance().exists(nonExistComponentBuildKey));
    }

    @Test
    public void componentBuildExistsTest(){
        assertTrue(ComponentBuildConfiguration.getInstance().exists(componentBuild.getMetadataKey()));
    }

    @Test
    public void componentBuildInsertTest() throws MetadataAlreadyExistsException {
        int nbBefore = ComponentBuildConfiguration.getInstance().getAll().size();
        ComponentBuild newComponentBuild = createComponentBuild();
        ComponentBuildConfiguration.getInstance().insert(newComponentBuild);
        int nbAfter = ComponentBuildConfiguration.getInstance().getAll().size();
        assertEquals(nbBefore, nbAfter - 1);
    }

    @Test
    public void componentBuildInsertAlreadyExistsTest() {
        assertThrows(MetadataAlreadyExistsException.class,() -> ComponentBuildConfiguration.getInstance().insert(componentBuild));
    }

    @Test
    public void componentBuildDeleteTest() throws MetadataDoesNotExistException {
        ComponentBuildConfiguration.getInstance().delete(componentBuild.getMetadataKey());
    }

    @Test
    public void componentBuildDeleteDoesNotExistTest() throws MetadataDoesNotExistException {
        ComponentBuild deleteComponentBuild = createComponentBuild();
        assertThrows(MetadataDoesNotExistException.class,() ->
                ComponentBuildConfiguration.getInstance().delete(deleteComponentBuild.getMetadataKey()));
    }

    @Test
    public void componentBuildGetTest() {
        Optional<ComponentBuild> newComponentBuild = ComponentBuildConfiguration.getInstance().get(componentBuild.getMetadataKey());
        assertTrue(newComponentBuild.isPresent());
        assertEquals(componentBuild.getMetadataKey().getComponentId(),
                newComponentBuild.get().getMetadataKey().getComponentId());
        assertEquals(componentBuild.getMetadataKey().getComponentVersionNb(),
                newComponentBuild.get().getMetadataKey().getComponentVersionNb());
        assertEquals(componentBuild.getMetadataKey().getComponentBuildName(),
                newComponentBuild.get().getMetadataKey().getComponentBuildName());
        assertEquals(componentBuild.getDescription(), newComponentBuild.get().getDescription());
    }

    @Test
    public void componentBuildGetNotExistsTest(){
        ComponentBuildKey componentBuildKey = new ComponentBuildKey("3", 1,
                "build name not exist");
        assertFalse(ComponentBuildConfiguration.getInstance().exists(componentBuildKey));
        assertFalse(ComponentBuildConfiguration.getInstance().get(componentBuildKey).isPresent());
    }

    @Test
    public void componentBuildUpdateTest() throws MetadataDoesNotExistException {
        ComponentBuild componentBuildUpdate = componentBuild;
        String newValue = "new description";
        componentBuildUpdate.setDescription(newValue);
        ComponentBuildConfiguration.getInstance().update(componentBuildUpdate);
        Optional<ComponentBuild> checkComponentBuild = ComponentBuildConfiguration.getInstance().get(componentBuildUpdate.getMetadataKey());
        assertTrue(checkComponentBuild.isPresent() && checkComponentBuild.get().getDescription().equals(newValue));
    }

    private ComponentBuild createComponentBuild(){
        ComponentBuild build = new ComponentBuild(new ComponentBuildKey("new",
                1, "create build"),
                "build of component");
        return build;
    }
}
