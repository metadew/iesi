package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import io.metadew.iesi.metadata.repository.RepositoryTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComponentParameterConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private ComponentParameter componentParameter11;
    private ComponentParameter componentParameter12;
    private ComponentParameter componentParameter2;
    private ComponentParameter componentParameter3;


    @BeforeEach
    void setup() {
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

    @AfterEach
    void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    void componentParameterNotExistsTest() {
        assertFalse(ComponentParameterConfiguration.getInstance().exists(componentParameter11));
    }

    @Test
    void componentParameterExistsTest() throws MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        assertTrue(ComponentParameterConfiguration.getInstance().exists(componentParameter11.getMetadataKey()));
    }

    @Test
    void componentParameterInsertOnlyTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentParameterConfiguration.getInstance().insert(componentParameter11);

        assertEquals(1, ComponentParameterConfiguration.getInstance().getAll().size());
        Optional<ComponentParameter> fetchedComponentParameter = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter.isPresent());
        assertEquals(componentParameter11, fetchedComponentParameter.get());
    }

    @Test
    void componentParameterInsertMultipleTest() throws MetadataAlreadyExistsException {
        assertEquals(0, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        ComponentParameterConfiguration.getInstance().insert(componentParameter3);

        assertEquals(4, ComponentParameterConfiguration.getInstance().getAll().size());
        Optional<ComponentParameter> fetchedComponentParameter11 = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals(componentParameter11, fetchedComponentParameter11.get());

        Optional<ComponentParameter> fetchedComponentParameter12 = ComponentParameterConfiguration.getInstance().get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals(componentParameter12, fetchedComponentParameter12.get());

        Optional<ComponentParameter> fetchedComponentParameter2 = ComponentParameterConfiguration.getInstance().get(componentParameter2.getMetadataKey());
        assertTrue(fetchedComponentParameter2.isPresent());
        assertEquals(componentParameter2, fetchedComponentParameter2.get());

        Optional<ComponentParameter> fetchedComponentParameter3 = ComponentParameterConfiguration.getInstance().get(componentParameter3.getMetadataKey());
        assertTrue(fetchedComponentParameter3.isPresent());
        assertEquals(componentParameter3, fetchedComponentParameter3.get());
    }

    @Test
    void componentParameterInsertAlreadyExistsTest() throws MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        assertThrows(MetadataAlreadyExistsException.class,() -> ComponentParameterConfiguration.getInstance().insert(componentParameter11));
    }

    @Test
    void componentParameterDeleteTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        assertEquals(1, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentParameterConfiguration.getInstance().delete(componentParameter11.getMetadataKey());
        assertEquals(0, ComponentParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    void componentParameterDeleteMultipleTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        assertEquals(2, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentParameterConfiguration.getInstance().delete(componentParameter11.getMetadataKey());
        assertEquals(1, ComponentParameterConfiguration.getInstance().getAll().size());

        Optional<ComponentParameter> fetchedComponentParameter12 = ComponentParameterConfiguration.getInstance().get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals(componentParameter12, fetchedComponentParameter12.get());
    }

    @Test
    void componentParameterDeleteMultipleVersionsTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        assertEquals(3, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentParameterConfiguration.getInstance().delete(componentParameter11.getMetadataKey());
        assertEquals(2, ComponentParameterConfiguration.getInstance().getAll().size());

        Optional<ComponentParameter> fetchedComponentParameter12 = ComponentParameterConfiguration.getInstance().get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals(componentParameter12, fetchedComponentParameter12.get());

        Optional<ComponentParameter> fetchedComponentParameter2 = ComponentParameterConfiguration.getInstance().get(componentParameter2.getMetadataKey());
        assertTrue(fetchedComponentParameter2.isPresent());
        assertEquals(componentParameter2, fetchedComponentParameter2.get());

    }

    @Test
    void componentParameterDeleteMultipleVersionsAndIdTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        ComponentParameterConfiguration.getInstance().insert(componentParameter3);
        assertEquals(4, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentParameterConfiguration.getInstance().delete(componentParameter2.getMetadataKey());
        assertEquals(3, ComponentParameterConfiguration.getInstance().getAll().size());

        Optional<ComponentParameter> fetchedComponentParameter11 = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals(componentParameter11, fetchedComponentParameter11.get());

        Optional<ComponentParameter> fetchedComponentParameter12 = ComponentParameterConfiguration.getInstance().get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals(componentParameter12, fetchedComponentParameter12.get());

        Optional<ComponentParameter> fetchedComponentParameter3 = ComponentParameterConfiguration.getInstance().get(componentParameter3.getMetadataKey());
        assertTrue(fetchedComponentParameter3.isPresent());
        assertEquals(componentParameter3, fetchedComponentParameter3.get());
    }

    @Test
    void componentParameterDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() ->
                ComponentParameterConfiguration.getInstance().delete(componentParameter11.getMetadataKey()));
    }

    @Test
    void componentParameterGetNotExistsTest(){
        assertFalse(ComponentParameterConfiguration.getInstance().exists(componentParameter11));
        assertFalse(ComponentParameterConfiguration.getInstance().get(componentParameter2.getMetadataKey()).isPresent());
    }

    @Test
    void componentParameterUpdateTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);

        Optional<ComponentParameter> fetchedComponentParameter11 = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("value", fetchedComponentParameter11.get().getValue());

        componentParameter11.setValue("dummy");
        ComponentParameterConfiguration.getInstance().update(componentParameter11);

        fetchedComponentParameter11 = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("dummy", fetchedComponentParameter11.get().getValue());
    }

    @Test
    void componentParameterUpdateMultipleVersionsTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);

        Optional<ComponentParameter> fetchedComponentParameter11 = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("value", fetchedComponentParameter11.get().getValue());

        Optional<ComponentParameter> fetchedComponentParameter12 = ComponentParameterConfiguration.getInstance().get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals("value", fetchedComponentParameter12.get().getValue());

        componentParameter11.setValue("dummy");
        ComponentParameterConfiguration.getInstance().update(componentParameter11);

        fetchedComponentParameter11 = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("dummy", fetchedComponentParameter11.get().getValue());

        fetchedComponentParameter12 = ComponentParameterConfiguration.getInstance().get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals("value", fetchedComponentParameter12.get().getValue());
    }

    @Test
    void componentParameterUpdateMultipleTest() throws MetadataDoesNotExistException, MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        ComponentParameterConfiguration.getInstance().insert(componentParameter3);

        Optional<ComponentParameter> fetchedComponentParameter11 = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("value", fetchedComponentParameter11.get().getValue());

        Optional<ComponentParameter> fetchedComponentParameter12 = ComponentParameterConfiguration.getInstance().get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals("value", fetchedComponentParameter12.get().getValue());

        Optional<ComponentParameter> fetchedComponentParameter2 = ComponentParameterConfiguration.getInstance().get(componentParameter2.getMetadataKey());
        assertTrue(fetchedComponentParameter2.isPresent());
        assertEquals("value", fetchedComponentParameter2.get().getValue());

        Optional<ComponentParameter> fetchedComponentParameter3 = ComponentParameterConfiguration.getInstance().get(componentParameter3.getMetadataKey());
        assertTrue(fetchedComponentParameter3.isPresent());
        assertEquals("value", fetchedComponentParameter3.get().getValue());

        componentParameter2.setValue("dummy");
        ComponentParameterConfiguration.getInstance().update(componentParameter2);

        fetchedComponentParameter11 = ComponentParameterConfiguration.getInstance().get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("value", fetchedComponentParameter11.get().getValue());

        fetchedComponentParameter12 = ComponentParameterConfiguration.getInstance().get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals("value", fetchedComponentParameter12.get().getValue());

        fetchedComponentParameter2 = ComponentParameterConfiguration.getInstance().get(componentParameter2.getMetadataKey());
        assertTrue(fetchedComponentParameter2.isPresent());
        assertEquals("dummy", fetchedComponentParameter2.get().getValue());

        fetchedComponentParameter3 = ComponentParameterConfiguration.getInstance().get(componentParameter3.getMetadataKey());
        assertTrue(fetchedComponentParameter3.isPresent());
        assertEquals("value", fetchedComponentParameter3.get().getValue());
    }

    @Test
    void componentParameterGetByComponentTest() throws MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        ComponentParameterConfiguration.getInstance().insert(componentParameter3);

        assertEquals(Stream.of(componentParameter11, componentParameter12).collect(Collectors.toList()),
                ComponentParameterConfiguration.getInstance().getByComponent(componentParameter11.getMetadataKey().getComponentKey()));
    }

    @Test
    void componentParameterGetByComponent2Test() throws MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        ComponentParameterConfiguration.getInstance().insert(componentParameter3);

        assertEquals(Stream.of(componentParameter2).collect(Collectors.toList()),
                ComponentParameterConfiguration.getInstance().getByComponent(componentParameter2.getMetadataKey().getComponentKey()));
    }

    @Test
    void componentParameterGetByComponent3Test() throws MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        ComponentParameterConfiguration.getInstance().insert(componentParameter3);

        assertEquals(Stream.of(componentParameter3).collect(Collectors.toList()),
                ComponentParameterConfiguration.getInstance().getByComponent(componentParameter3.getMetadataKey().getComponentKey()));
    }

    @Test
    void componentParameterDeleteByComponentTest() throws MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        ComponentParameterConfiguration.getInstance().insert(componentParameter3);

        assertEquals(4, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentParameterConfiguration.getInstance().deleteByComponent(componentParameter11.getMetadataKey().getComponentKey());

        assertEquals(2, ComponentParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    void componentParameterDeleteByComponent2Test() throws MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        ComponentParameterConfiguration.getInstance().insert(componentParameter3);

        assertEquals(4, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentParameterConfiguration.getInstance().deleteByComponent(componentParameter2.getMetadataKey().getComponentKey());

        assertEquals(3, ComponentParameterConfiguration.getInstance().getAll().size());
    }

    @Test
    void componentParameterDeleteByComponent3Test() throws MetadataAlreadyExistsException {
        ComponentParameterConfiguration.getInstance().insert(componentParameter11);
        ComponentParameterConfiguration.getInstance().insert(componentParameter12);
        ComponentParameterConfiguration.getInstance().insert(componentParameter2);
        ComponentParameterConfiguration.getInstance().insert(componentParameter3);

        assertEquals(4, ComponentParameterConfiguration.getInstance().getAll().size());

        ComponentParameterConfiguration.getInstance().deleteByComponent(componentParameter3.getMetadataKey().getComponentKey());

        assertEquals(3, ComponentParameterConfiguration.getInstance().getAll().size());
    }
}
