package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.repository.DesignMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ComponentAttributeConfiguration.class)
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ComponentAttributeConfigurationTest {

    private DesignMetadataRepository designMetadataRepository;
    private ComponentAttribute componentAttribute1;
    private ComponentAttribute componentAttribute2;
    private ComponentAttribute componentAttribute3;
    private ComponentAttribute componentAttribute4;
    private ComponentAttribute componentAttribute5;

    @Autowired
    private ComponentAttributeConfiguration configuration;

    @BeforeEach
    void setup() {
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

    @Test
    void componentAttributeNotExistsTest() {
        assertFalse(configuration.exists(componentAttribute1));
    }

    @Test
    void componentAttributeExistsTest() {
        configuration.insert(componentAttribute1);
        assertTrue(configuration.exists(componentAttribute1.getMetadataKey()));
    }

    @Test
    void componentAttributeInsertTest() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        assertEquals(5, configuration.getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = configuration.get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals(componentAttribute1, fetchedComponentAttribute1.get());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = configuration.get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals(componentAttribute2, fetchedComponentAttribute2.get());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = configuration.get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals(componentAttribute3, fetchedComponentAttribute3.get());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = configuration.get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals(componentAttribute4, fetchedComponentAttribute4.get());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = configuration.get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals(componentAttribute5, fetchedComponentAttribute5.get());
    }

    @Test
    void componentAttributeInsertAlreadyExistsTest() {
        configuration.insert(componentAttribute1);
        assertThrows(MetadataAlreadyExistsException.class, () -> configuration.insert(componentAttribute1));
    }

    @Test
    void componentAttributeDeleteTest() {
        configuration.insert(componentAttribute1);
        assertEquals(1, configuration.getAll().size());
        configuration.delete(componentAttribute1.getMetadataKey());
        assertEquals(0, configuration.getAll().size());
    }

    @Test
    void componentAttributeDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () ->
                configuration.delete(componentAttribute1.getMetadataKey()));
    }


    @Test
    void componentAttributeGetNotExistsTest() {
        assertFalse(configuration.exists(componentAttribute1));
        assertFalse(configuration.get(componentAttribute1.getMetadataKey()).isPresent());
    }

    @Test
    void componentAttributeUpdate1Test() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        assertEquals(5, configuration.getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = configuration.get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = configuration.get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = configuration.get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = configuration.get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = configuration.get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

        componentAttribute1.setValue("dummy");
        configuration.update(componentAttribute1);

        fetchedComponentAttribute1 = configuration.get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("dummy", fetchedComponentAttribute1.get().getValue());

        fetchedComponentAttribute2 = configuration.get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        fetchedComponentAttribute3 = configuration.get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        fetchedComponentAttribute4 = configuration.get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        fetchedComponentAttribute5 = configuration.get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

    }

    @Test
    void componentAttributeUpdate2Test() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        assertEquals(5, configuration.getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = configuration.get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = configuration.get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = configuration.get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = configuration.get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = configuration.get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

        componentAttribute3.setValue("dummy");
        configuration.update(componentAttribute3);

        fetchedComponentAttribute1 = configuration.get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        fetchedComponentAttribute2 = configuration.get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        fetchedComponentAttribute3 = configuration.get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("dummy", fetchedComponentAttribute3.get().getValue());

        fetchedComponentAttribute4 = configuration.get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        fetchedComponentAttribute5 = configuration.get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());
    }


    @Test
    void componentAttributeUpdate3Test() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        assertEquals(5, configuration.getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = configuration.get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = configuration.get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = configuration.get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = configuration.get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = configuration.get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

        componentAttribute4.setValue("dummy");
        configuration.update(componentAttribute4);

        fetchedComponentAttribute1 = configuration.get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        fetchedComponentAttribute2 = configuration.get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        fetchedComponentAttribute3 = configuration.get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        fetchedComponentAttribute4 = configuration.get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("dummy", fetchedComponentAttribute4.get().getValue());

        fetchedComponentAttribute5 = configuration.get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

    }


    @Test
    void componentAttributeUpdate4Test() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        assertEquals(5, configuration.getAll().size());

        Optional<ComponentAttribute> fetchedComponentAttribute1 = configuration.get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute2 = configuration.get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute3 = configuration.get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute4 = configuration.get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        Optional<ComponentAttribute> fetchedComponentAttribute5 = configuration.get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("test", fetchedComponentAttribute5.get().getValue());

        componentAttribute5.setValue("dummy");
        configuration.update(componentAttribute5);

        fetchedComponentAttribute1 = configuration.get(componentAttribute1.getMetadataKey());
        assertTrue(fetchedComponentAttribute1.isPresent());
        assertEquals("test", fetchedComponentAttribute1.get().getValue());

        fetchedComponentAttribute2 = configuration.get(componentAttribute2.getMetadataKey());
        assertTrue(fetchedComponentAttribute2.isPresent());
        assertEquals("test", fetchedComponentAttribute2.get().getValue());

        fetchedComponentAttribute3 = configuration.get(componentAttribute3.getMetadataKey());
        assertTrue(fetchedComponentAttribute3.isPresent());
        assertEquals("test", fetchedComponentAttribute3.get().getValue());

        fetchedComponentAttribute4 = configuration.get(componentAttribute4.getMetadataKey());
        assertTrue(fetchedComponentAttribute4.isPresent());
        assertEquals("test", fetchedComponentAttribute4.get().getValue());

        fetchedComponentAttribute5 = configuration.get(componentAttribute5.getMetadataKey());
        assertTrue(fetchedComponentAttribute5.isPresent());
        assertEquals("dummy", fetchedComponentAttribute5.get().getValue());

    }


    @Test
    void componentAttributeGetByComponentTest() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        assertEquals(Stream.of(componentAttribute1, componentAttribute2, componentAttribute3).collect(Collectors.toList()),
                configuration.getByComponent(componentAttribute1.getMetadataKey().getComponentKey()));

    }

    @Test
    void componentAttributeGetByComponentAndEnvironmentTest() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        assertEquals(Stream.of(componentAttribute1, componentAttribute2).collect(Collectors.toList()),
                configuration.getByComponentAndEnvironment(componentAttribute1.getMetadataKey().getComponentKey(),
                        componentAttribute1.getMetadataKey().getEnvironmentKey()));
    }


    @Test
    void componentAttributeDeleteByComponentTest() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        configuration.deleteByComponent(componentAttribute1.getMetadataKey().getComponentKey());

        assertEquals(Stream.of(componentAttribute4, componentAttribute5).collect(Collectors.toList()),
                configuration.getAll());
    }

    @Test
    void componentAttributeDeleteByComponentAndEnvironmentTest() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        configuration.deleteByComponentAndEnvironment(componentAttribute1.getMetadataKey().getComponentKey(),
                componentAttribute1.getMetadataKey().getEnvironmentKey());

        assertEquals(Stream.of(componentAttribute3, componentAttribute4, componentAttribute5).collect(Collectors.toList()),
                configuration.getAll());
    }


    @Test
    void componentAttributeDeleteByComponentIdTest() {
        configuration.insert(componentAttribute1);
        configuration.insert(componentAttribute2);
        configuration.insert(componentAttribute3);
        configuration.insert(componentAttribute4);
        configuration.insert(componentAttribute5);

        configuration.deleteByComponentId(componentAttribute1.getMetadataKey().getComponentKey().getId());

        assertEquals(Stream.of(componentAttribute5).collect(Collectors.toList()),
                configuration.getAll());
    }

}
