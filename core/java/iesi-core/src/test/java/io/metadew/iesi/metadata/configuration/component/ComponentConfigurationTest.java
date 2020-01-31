package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComponentConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private Component component1;
    private Component component2;
    private Component component3;


    @BeforeEach
    void setup() {
        component1 = new ComponentBuilder("1", 1)
                .numberOfAttributes(2)
                .numberOfParameters(3)
                .description("test")
                .name("comp1")
                .build();
        component2 = new ComponentBuilder("1", 2)
                .numberOfAttributes(2)
                .numberOfParameters(3)
                .name("comp1")
                .description("test")
                .build();
        component3 = new ComponentBuilder("2", 1)
                .numberOfAttributes(2)
                .numberOfParameters(3)
                .name("comp2")
                .description("test")
                .build();

        designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
    }

    @AfterEach
    void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    void componentNotExistsTest() {
        assertFalse(ComponentConfiguration.getInstance().exists(component1));
    }

    @Test
    void componentExistsTest() {
        ComponentConfiguration.getInstance().insert(component1);
        assertTrue(ComponentConfiguration.getInstance().exists(component1.getMetadataKey()));
    }

    @Test
    void componentInsertTest() {
        ComponentConfiguration.getInstance().insert(component1);
        assertEquals(1, ComponentConfiguration.getInstance().getAll().size());

        Optional<Component> fetchedComponentVersion1 = ComponentConfiguration.getInstance().get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals(component1, fetchedComponentVersion1.get());

    }

    @Test
    void componentInsertAlreadyExistsTest() {
        ComponentConfiguration.getInstance().insert(component1);
        assertThrows(MetadataAlreadyExistsException.class,() -> ComponentConfiguration.getInstance().insert(component1));
    }

    @Test
    void componentDeleteTest() {
        ComponentConfiguration.getInstance().insert(component1);
        ComponentConfiguration.getInstance().insert(component2);
        ComponentConfiguration.getInstance().insert(component3);
        System.out.println(ComponentConfiguration.getInstance().getAll());
        assertEquals(3, ComponentConfiguration.getInstance().getAll().size());

        ComponentConfiguration.getInstance().delete(component1.getMetadataKey());

        assertEquals(2, ComponentConfiguration.getInstance().getAll().size());
        assertEquals(6, ComponentParameterConfiguration.getInstance().getAll().size());
        assertEquals(2, ComponentVersionConfiguration.getInstance().getAll().size());
        assertEquals(4, ComponentAttributeConfiguration.getInstance().getAll().size());

        Optional<Component> fetchedComponentVersion2 = ComponentConfiguration.getInstance().get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals(component2, fetchedComponentVersion2.get());
        Optional<Component> fetchedComponentVersion3 = ComponentConfiguration.getInstance().get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals(component3, fetchedComponentVersion3.get());
    }

    @Test
    void componentDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() ->
                ComponentConfiguration.getInstance().delete(component1.getMetadataKey()));
    }

    @Test
    void componentGetNotExistsTest(){
        assertFalse(ComponentConfiguration.getInstance().exists(component1));
        assertFalse(ComponentConfiguration.getInstance().get(component1.getMetadataKey()).isPresent());
    }

    @Test
    void componentUpdate1Test() {
        ComponentConfiguration.getInstance().insert(component1);
        ComponentConfiguration.getInstance().insert(component2);
        ComponentConfiguration.getInstance().insert(component3);

        Optional<Component> fetchedComponentVersion1 = ComponentConfiguration.getInstance().get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<Component> fetchedComponentVersion2 = ComponentConfiguration.getInstance().get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<Component> fetchedComponentVersion3 = ComponentConfiguration.getInstance().get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        component1.setDescription("dummy");
        ComponentConfiguration.getInstance().update(component1);

        fetchedComponentVersion1 = ComponentConfiguration.getInstance().get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("dummy", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = ComponentConfiguration.getInstance().get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("dummy", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = ComponentConfiguration.getInstance().get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentUpdate2Test() {
        ComponentConfiguration.getInstance().insert(component1);
        ComponentConfiguration.getInstance().insert(component2);
        ComponentConfiguration.getInstance().insert(component3);

        Optional<Component> fetchedComponentVersion1 = ComponentConfiguration.getInstance().get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<Component> fetchedComponentVersion2 = ComponentConfiguration.getInstance().get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<Component> fetchedComponentVersion3 = ComponentConfiguration.getInstance().get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        component2.setDescription("dummy");
        ComponentConfiguration.getInstance().update(component2);

        fetchedComponentVersion1 = ComponentConfiguration.getInstance().get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("dummy", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = ComponentConfiguration.getInstance().get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("dummy", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = ComponentConfiguration.getInstance().get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentUpdate3Test() {
        ComponentConfiguration.getInstance().insert(component1);
        ComponentConfiguration.getInstance().insert(component2);
        ComponentConfiguration.getInstance().insert(component3);

        Optional<Component> fetchedComponentVersion1 = ComponentConfiguration.getInstance().get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<Component> fetchedComponentVersion2 = ComponentConfiguration.getInstance().get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<Component> fetchedComponentVersion3 = ComponentConfiguration.getInstance().get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        component3.setDescription("dummy");
        ComponentConfiguration.getInstance().update(component3);

        fetchedComponentVersion1 = ComponentConfiguration.getInstance().get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = ComponentConfiguration.getInstance().get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = ComponentConfiguration.getInstance().get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("dummy", fetchedComponentVersion3.get().getDescription());
    }

//    @Test
//    void componentGetByComponentId1Test() {
//        ComponentConfiguration.getInstance().insert(component1);
//        ComponentConfiguration.getInstance().insert(component2);
//        ComponentConfiguration.getInstance().insert(component3);
//
//        assertEquals(Stream.of(component1, component2).collect(Collectors.toList()),
//                ComponentConfiguration.getInstance().getByComponent(component1.getMetadataKey().getComponentKey().getId()));
//    }
//    @Test
//    void componentGetByComponentId2Test() {
//        ComponentConfiguration.getInstance().insert(component1);
//        ComponentConfiguration.getInstance().insert(component2);
//        ComponentConfiguration.getInstance().insert(component3);
//
//        assertEquals(Stream.of(component3).collect(Collectors.toList()),
//                ComponentConfiguration.getInstance().getByComponent(component3.getMetadataKey().getComponentKey().getId()));
//    }
//
//    @Test
//    void componentDeleteByComponentId1Test() throws MetadataAlreadyExistsException, MetadataDoesNotExistException {
//        ComponentConfiguration.getInstance().insert(component1);
//        ComponentConfiguration.getInstance().insert(component2);
//        ComponentConfiguration.getInstance().insert(component3);
//
//        ComponentConfiguration.getInstance().deleteByComponent(component3.getMetadataKey().getComponentKey());
//
//        assertEquals(Stream.of(component1, component2).collect(Collectors.toList()),
//                ComponentConfiguration.getInstance().getByComponent(component1.getMetadataKey().getComponentKey().getId()));
//    }
//    @Test
//    void componentDeleteByComponentId2Test() throws MetadataAlreadyExistsException, MetadataDoesNotExistException {
//        ComponentConfiguration.getInstance().insert(component1);
//        ComponentConfiguration.getInstance().insert(component2);
//        ComponentConfiguration.getInstance().insert(component3);
//
//        ComponentConfiguration.getInstance().deleteByComponent(component1.getMetadataKey().getComponentKey());
//
//        assertEquals(Stream.of(component3).collect(Collectors.toList()),
//                ComponentConfiguration.getInstance().getByComponent(component3.getMetadataKey().getComponentKey().getId()));
//    }

    @Test
    void componentDeleteAllTest() {
        ComponentConfiguration.getInstance().insert(component1);
        ComponentConfiguration.getInstance().insert(component2);
        ComponentConfiguration.getInstance().insert(component3);

        assertEquals(3, ComponentConfiguration.getInstance().getAll().size());
        assertEquals(3, ComponentVersionConfiguration.getInstance().getAll().size());
        assertEquals(6, ComponentAttributeConfiguration.getInstance().getAll().size());
        assertEquals(9, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentConfiguration.getInstance().deleteAll();

        assertEquals(0, ComponentConfiguration.getInstance().getAll().size());
        assertEquals(0, ComponentVersionConfiguration.getInstance().getAll().size());
        assertEquals(0, ComponentAttributeConfiguration.getInstance().getAll().size());
        assertEquals(0, ComponentParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    void componentGetLatestVersionAllTest() throws MetadataAlreadyExistsException, MetadataDoesNotExistException {
        ComponentConfiguration.getInstance().insert(component1);
        ComponentConfiguration.getInstance().insert(component2);
        ComponentConfiguration.getInstance().insert(component3);

        Optional<Component> fetchedComponent = ComponentConfiguration.getInstance().get(component1.getMetadataKey().getId());
        assertTrue(fetchedComponent.isPresent());
        assertEquals(component2, fetchedComponent.get());
    }
    }
