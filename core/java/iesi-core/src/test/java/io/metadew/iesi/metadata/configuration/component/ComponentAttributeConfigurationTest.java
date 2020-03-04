package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
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

class ComponentAttributeConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private ComponentAttribute componentAttribute1;
    private ComponentAttribute componentAttribute2;
    private ComponentAttribute componentAttribute3;
    private ComponentAttribute componentAttribute4;
    private ComponentAttribute componentAttribute5;

    @BeforeEach
    void setup() {
        this.designMetadataRepository = RepositoryTestSetup.getDesignMetadataRepository();
        componentAttribute1 = new ComponentAttributeBuilder("1", 1, "env1",
                "attribute name 1")
                .value("test")
                .build();
        componentAttribute2 = new ComponentAttributeBuilder("1", 1, "env1",
                "attribute name 2")
                .value("test")
                .build();
        componentAttribute3 = new ComponentAttributeBuilder("1", 1, "env2",
                "attribute name 1")
                .value("test")
                .build();
        componentAttribute4 = new ComponentAttributeBuilder("1", 2, "env1",
                "attribute name 1")
                .value("test")
                .build();
        componentAttribute5 = new ComponentAttributeBuilder("2", 1, "env1",
                "attribute name 1")
                .value("test")
                .build();
    }

    @AfterEach
    void clearDatabase() {
        // drop because the designMetadataRepository already is initialized so you can't recreate those tables
        // in the initializer unless you delete the tables after each test
        designMetadataRepository.dropAllTables();
    }

    @Test
    void componentAttributeNotExistsTest() {
        assertFalse(ComponentAttributeConfiguration.getInstance().exists(componentAttribute1));
    }

    @Test
    void componentAttributeExistsTest() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        assertTrue(ComponentAttributeConfiguration.getInstance().exists(componentAttribute1.getMetadataKey()));
    }

    @Test
    void componentAttributeInsertTest() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        assertEquals(5, ComponentAttributeConfiguration.getInstance().getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals(componentAttribute1, fetchedComponentAttribute1.get());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = ComponentAttributeConfiguration.getInstance().get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals(componentAttribute2, fetchedComponentAttribute2.get());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = ComponentAttributeConfiguration.getInstance().get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals(componentAttribute3, fetchedComponentAttribute3.get());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = ComponentAttributeConfiguration.getInstance().get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals(componentAttribute4, fetchedComponentAttribute4.get());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = ComponentAttributeConfiguration.getInstance().get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals(componentAttribute5, fetchedComponentAttribute5.get());
    }

    @Test
    void componentAttributeInsertAlreadyExistsTest() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        assertThrows(MetadataAlreadyExistsException.class, () -> ComponentAttributeConfiguration.getInstance().insert(componentAttribute1));
    }

    @Test
    void componentAttributeDeleteTest() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        assertEquals(1, ComponentAttributeConfiguration.getInstance().getAll().size());
        ComponentAttributeConfiguration.getInstance().delete(componentAttribute1.getMetadataKey());
        assertEquals(0, ComponentAttributeConfiguration.getInstance().getAll().size());
    }

    @Test
    void componentAttributeDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () ->
                ComponentAttributeConfiguration.getInstance().delete(componentAttribute1.getMetadataKey()));
    }


    @Test
    void componentAttributeGetNotExistsTest() {
        assertFalse(ComponentAttributeConfiguration.getInstance().exists(componentAttribute1));
        assertFalse(ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey()).isPresent());
    }

    @Test
    void componentAttributeUpdate1Test() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        assertEquals(5, ComponentAttributeConfiguration.getInstance().getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = ComponentAttributeConfiguration.getInstance().get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = ComponentAttributeConfiguration.getInstance().get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = ComponentAttributeConfiguration.getInstance().get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = ComponentAttributeConfiguration.getInstance().get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

        componentAttribute1.setValue("dummy");
        ComponentAttributeConfiguration.getInstance().update(componentAttribute1);

        fetchedComponentAttribute1 = ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("dummy", fetchedComponentAttribute1.get().getValue());

        fetchedComponentAttribute2 = ComponentAttributeConfiguration.getInstance().get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        fetchedComponentAttribute3 = ComponentAttributeConfiguration.getInstance().get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        fetchedComponentAttribute4 = ComponentAttributeConfiguration.getInstance().get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        fetchedComponentAttribute5 = ComponentAttributeConfiguration.getInstance().get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

    }

    @Test
    void componentAttributeUpdate2Test() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        assertEquals(5, ComponentAttributeConfiguration.getInstance().getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = ComponentAttributeConfiguration.getInstance().get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = ComponentAttributeConfiguration.getInstance().get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = ComponentAttributeConfiguration.getInstance().get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = ComponentAttributeConfiguration.getInstance().get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

        componentAttribute3.setValue("dummy");
        ComponentAttributeConfiguration.getInstance().update(componentAttribute3);

        fetchedComponentAttribute1 = ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        fetchedComponentAttribute2 = ComponentAttributeConfiguration.getInstance().get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        fetchedComponentAttribute3 = ComponentAttributeConfiguration.getInstance().get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("dummy", fetchedComponentAttribute3.get().getValue());

        fetchedComponentAttribute4 = ComponentAttributeConfiguration.getInstance().get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        fetchedComponentAttribute5 = ComponentAttributeConfiguration.getInstance().get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());
    }


    @Test
    void componentAttributeUpdate3Test() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        assertEquals(5, ComponentAttributeConfiguration.getInstance().getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = ComponentAttributeConfiguration.getInstance().get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = ComponentAttributeConfiguration.getInstance().get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = ComponentAttributeConfiguration.getInstance().get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = ComponentAttributeConfiguration.getInstance().get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

        componentAttribute4.setValue("dummy");
        ComponentAttributeConfiguration.getInstance().update(componentAttribute4);

        fetchedComponentAttribute1 = ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        fetchedComponentAttribute2 = ComponentAttributeConfiguration.getInstance().get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        fetchedComponentAttribute3 = ComponentAttributeConfiguration.getInstance().get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        fetchedComponentAttribute4 = ComponentAttributeConfiguration.getInstance().get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("dummy", fetchedComponentAttribute4.get().getValue());

        fetchedComponentAttribute5 = ComponentAttributeConfiguration.getInstance().get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

    }


    @Test
    void componentAttributeUpdate4Test() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        assertEquals(5, ComponentAttributeConfiguration.getInstance().getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = ComponentAttributeConfiguration.getInstance().get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = ComponentAttributeConfiguration.getInstance().get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = ComponentAttributeConfiguration.getInstance().get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = ComponentAttributeConfiguration.getInstance().get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

        componentAttribute5.setValue("dummy");
        ComponentAttributeConfiguration.getInstance().update(componentAttribute5);

        fetchedComponentAttribute1 = ComponentAttributeConfiguration.getInstance().get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        fetchedComponentAttribute2 = ComponentAttributeConfiguration.getInstance().get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        fetchedComponentAttribute3 = ComponentAttributeConfiguration.getInstance().get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        fetchedComponentAttribute4 = ComponentAttributeConfiguration.getInstance().get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        fetchedComponentAttribute5 = ComponentAttributeConfiguration.getInstance().get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("dummy", fetchedComponentAttribute5.get().getValue());

    }


    @Test
    void componentAttributeGetByComponentTest() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        assertEquals(Stream.of(componentAttribute1, componentAttribute2, componentAttribute3).collect(Collectors.toList()),
                ComponentAttributeConfiguration.getInstance().getByComponent(componentAttribute1.getMetadataKey().getComponentKey()));

    }
    @Test
    void componentAttributeGetByComponentAndEnvironmentTest() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        assertEquals(Stream.of(componentAttribute1, componentAttribute2).collect(Collectors.toList()),
                ComponentAttributeConfiguration.getInstance().getByComponentAndEnvironment(componentAttribute1.getMetadataKey().getComponentKey(),
                        componentAttribute1.getMetadataKey().getEnvironmentKey()));
    }


    @Test
    void componentAttributeDeleteByComponentTest() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        ComponentAttributeConfiguration.getInstance().deleteByComponent(componentAttribute1.getMetadataKey().getComponentKey());

        assertEquals(Stream.of(componentAttribute4, componentAttribute5).collect(Collectors.toList()),
                ComponentAttributeConfiguration.getInstance().getAll());
    }

    @Test
    void componentAttributeDeleteByComponentAndEnvironmentTest() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        ComponentAttributeConfiguration.getInstance().deleteByComponentAndEnvironment(componentAttribute1.getMetadataKey().getComponentKey(),
                componentAttribute1.getMetadataKey().getEnvironmentKey());

        assertEquals(Stream.of(componentAttribute3, componentAttribute4, componentAttribute5).collect(Collectors.toList()),
                ComponentAttributeConfiguration.getInstance().getAll());
    }


    @Test
    void componentAttributeDeleteByComponentIdTest() {
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute1);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute2);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute3);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute4);
        ComponentAttributeConfiguration.getInstance().insert(componentAttribute5);

        ComponentAttributeConfiguration.getInstance().deleteByComponentId(componentAttribute1.getMetadataKey().getComponentKey().getId());

        assertEquals(Stream.of(componentAttribute5).collect(Collectors.toList()),
                ComponentAttributeConfiguration.getInstance().getAll());
    }

}
