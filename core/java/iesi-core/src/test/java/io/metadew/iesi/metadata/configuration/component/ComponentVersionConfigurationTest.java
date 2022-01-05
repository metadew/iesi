package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComponentVersionConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private ComponentVersion componentVersion1;
    private ComponentVersion componentVersion2;
    private ComponentVersion componentVersion3;

    @BeforeAll
    static void prepare() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    @AfterEach
    void clearDatabase() {
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    @AfterAll
    static void teardown() {
        Configuration.getInstance();
        MetadataRepositoryConfiguration.getInstance()
                .getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }


    @BeforeEach
    void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        componentVersion1 = new ComponentVersionBuilder("1", 1)
                .description("test")
                .build();
        componentVersion2 = new ComponentVersionBuilder("1", 2)
                .description("test")
                .build();
        componentVersion3 = new ComponentVersionBuilder("2", 1)
                .description("test")
                .build();
    }

    @Test
    void componentVersionNotExistsTest() {
        assertFalse(ComponentVersionConfiguration.getInstance().exists(componentVersion1));
    }

    @Test
    void componentVersionExistsTest() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        assertTrue(ComponentVersionConfiguration.getInstance().exists(componentVersion1.getMetadataKey()));
    }

    @Test
    void componentVersionInsertTest() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        assertEquals(1, ComponentVersionConfiguration.getInstance().getAll().size());

        Optional<ComponentVersion> fetchedComponentVersion1 = ComponentVersionConfiguration.getInstance().get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals(componentVersion1, fetchedComponentVersion1.get());

    }

    @Test
    void componentVersionInsertAlreadyExistsTest() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        assertThrows(MetadataAlreadyExistsException.class,() -> ComponentVersionConfiguration.getInstance().insert(componentVersion1));
    }

    @Test
    void componentVersionDeleteTest() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        ComponentVersionConfiguration.getInstance().insert(componentVersion2);
        ComponentVersionConfiguration.getInstance().insert(componentVersion3);
        assertEquals(3, ComponentVersionConfiguration.getInstance().getAll().size());

        ComponentVersionConfiguration.getInstance().delete(componentVersion1.getMetadataKey());

        assertEquals(2, ComponentVersionConfiguration.getInstance().getAll().size());

        Optional<ComponentVersion> fetchedComponentVersion2 = ComponentVersionConfiguration.getInstance().get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals(componentVersion2, fetchedComponentVersion2.get());
        Optional<ComponentVersion> fetchedComponentVersion3 = ComponentVersionConfiguration.getInstance().get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals(componentVersion3, fetchedComponentVersion3.get());
    }

    @Test
    void componentVersionDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() ->
                ComponentVersionConfiguration.getInstance().delete(componentVersion1.getMetadataKey()));
    }

    @Test
    void componentVersionGetNotExistsTest(){
        assertFalse(ComponentVersionConfiguration.getInstance().exists(componentVersion1));
        assertFalse(ComponentVersionConfiguration.getInstance().get(componentVersion1.getMetadataKey()).isPresent());
    }

    @Test
    void componentVersionUpdate1Test() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        ComponentVersionConfiguration.getInstance().insert(componentVersion2);
        ComponentVersionConfiguration.getInstance().insert(componentVersion3);

        Optional<ComponentVersion> fetchedComponentVersion1 = ComponentVersionConfiguration.getInstance().get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion2 = ComponentVersionConfiguration.getInstance().get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion3 = ComponentVersionConfiguration.getInstance().get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        componentVersion1.setDescription("dummy");
        ComponentVersionConfiguration.getInstance().update(componentVersion1);

        fetchedComponentVersion1 = ComponentVersionConfiguration.getInstance().get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("dummy", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = ComponentVersionConfiguration.getInstance().get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = ComponentVersionConfiguration.getInstance().get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentVersionUpdate2Test() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        ComponentVersionConfiguration.getInstance().insert(componentVersion2);
        ComponentVersionConfiguration.getInstance().insert(componentVersion3);

        Optional<ComponentVersion> fetchedComponentVersion1 = ComponentVersionConfiguration.getInstance().get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion2 = ComponentVersionConfiguration.getInstance().get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion3 = ComponentVersionConfiguration.getInstance().get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        componentVersion2.setDescription("dummy");
        ComponentVersionConfiguration.getInstance().update(componentVersion2);

        fetchedComponentVersion1 = ComponentVersionConfiguration.getInstance().get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = ComponentVersionConfiguration.getInstance().get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("dummy", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = ComponentVersionConfiguration.getInstance().get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentVersionUpdate3Test() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        ComponentVersionConfiguration.getInstance().insert(componentVersion2);
        ComponentVersionConfiguration.getInstance().insert(componentVersion3);

        Optional<ComponentVersion> fetchedComponentVersion1 = ComponentVersionConfiguration.getInstance().get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion2 = ComponentVersionConfiguration.getInstance().get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion3 = ComponentVersionConfiguration.getInstance().get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        componentVersion3.setDescription("dummy");
        ComponentVersionConfiguration.getInstance().update(componentVersion3);

        fetchedComponentVersion1 = ComponentVersionConfiguration.getInstance().get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = ComponentVersionConfiguration.getInstance().get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = ComponentVersionConfiguration.getInstance().get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("dummy", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentVersionGetByComponentId1Test() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        ComponentVersionConfiguration.getInstance().insert(componentVersion2);
        ComponentVersionConfiguration.getInstance().insert(componentVersion3);

        assertEquals(Stream.of(componentVersion1, componentVersion2).collect(Collectors.toList()),
                ComponentVersionConfiguration.getInstance().getByComponent(componentVersion1.getMetadataKey().getComponentKey().getId()));
    }
    @Test
    void componentVersionGetByComponentId2Test() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        ComponentVersionConfiguration.getInstance().insert(componentVersion2);
        ComponentVersionConfiguration.getInstance().insert(componentVersion3);

        assertEquals(Stream.of(componentVersion3).collect(Collectors.toList()),
                ComponentVersionConfiguration.getInstance().getByComponent(componentVersion3.getMetadataKey().getComponentKey().getId()));
    }

    @Test
    void componentVersionDeleteByComponentId1Test() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        ComponentVersionConfiguration.getInstance().insert(componentVersion2);
        ComponentVersionConfiguration.getInstance().insert(componentVersion3);

        ComponentVersionConfiguration.getInstance().deleteByComponentId(componentVersion3.getMetadataKey().getComponentKey().getId());

        assertEquals(Stream.of(componentVersion1, componentVersion2).collect(Collectors.toList()),
                ComponentVersionConfiguration.getInstance().getByComponent(componentVersion1.getMetadataKey().getComponentKey().getId()));
    }

    @Test
    void componentVersionDeleteByComponentId2Test() {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        ComponentVersionConfiguration.getInstance().insert(componentVersion2);
        ComponentVersionConfiguration.getInstance().insert(componentVersion3);

        ComponentVersionConfiguration.getInstance().deleteByComponentId(componentVersion1.getMetadataKey().getComponentKey().getId());

        assertEquals(Stream.of(componentVersion3).collect(Collectors.toList()),
                ComponentVersionConfiguration.getInstance().getByComponent(componentVersion3.getMetadataKey().getComponentKey().getId()));
    }

    @Test
    void componentVersionGetLatestVersionByComponentIdTest() throws MetadataAlreadyExistsException, MetadataDoesNotExistException {
        ComponentVersionConfiguration.getInstance().insert(componentVersion1);
        ComponentVersionConfiguration.getInstance().insert(componentVersion2);
        ComponentVersionConfiguration.getInstance().insert(componentVersion3);

        Optional<ComponentVersion> fetchedComponentVersion2 = ComponentVersionConfiguration.getInstance().getLatestVersionByComponentId(componentVersion1.getMetadataKey().getComponentKey().getId());
        Optional<ComponentVersion> fetchedComponentVersion3 = ComponentVersionConfiguration.getInstance().getLatestVersionByComponentId(componentVersion3.getMetadataKey().getComponentKey().getId());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals(2, fetchedComponentVersion2.get().getMetadataKey().getComponentKey().getVersionNumber());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals(1, fetchedComponentVersion3.get().getMetadataKey().getComponentKey().getVersionNumber());
    }

}
