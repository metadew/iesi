package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
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

@SpringBootTest(classes = { ComponentParameterConfiguration.class, })
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ComponentParameterConfigurationTest {

    private ComponentParameter componentParameter11;
    private ComponentParameter componentParameter12;
    private ComponentParameter componentParameter2;
    private ComponentParameter componentParameter3;


    @Autowired
    private ComponentParameterConfiguration componentParameterConfiguration;

    @BeforeEach
    void setup() {
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

    @Test
    void componentParameterNotExistsTest() {
        assertFalse(componentParameterConfiguration.exists(componentParameter11));
    }

    @Test
    void componentParameterExistsTest() {
        componentParameterConfiguration.insert(componentParameter11);
        assertTrue(componentParameterConfiguration.exists(componentParameter11.getMetadataKey()));
    }

    @Test
    void componentParameterInsertOnlyTest() {
        assertEquals(0, componentParameterConfiguration.getAll().size());

        componentParameterConfiguration.insert(componentParameter11);

        assertEquals(1, componentParameterConfiguration.getAll().size());
        Optional<ComponentParameter> fetchedComponentParameter = componentParameterConfiguration.get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter.isPresent());
        assertEquals(componentParameter11, fetchedComponentParameter.get());
    }

    @Test
    void componentParameterInsertMultipleTest() {
        assertEquals(0, componentParameterConfiguration.getAll().size());

        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        componentParameterConfiguration.insert(componentParameter3);

        assertEquals(4, componentParameterConfiguration.getAll().size());
        Optional<ComponentParameter> fetchedComponentParameter11 = componentParameterConfiguration.get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals(componentParameter11, fetchedComponentParameter11.get());

        Optional<ComponentParameter> fetchedComponentParameter12 = componentParameterConfiguration.get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals(componentParameter12, fetchedComponentParameter12.get());

        Optional<ComponentParameter> fetchedComponentParameter2 = componentParameterConfiguration.get(componentParameter2.getMetadataKey());
        assertTrue(fetchedComponentParameter2.isPresent());
        assertEquals(componentParameter2, fetchedComponentParameter2.get());

        Optional<ComponentParameter> fetchedComponentParameter3 = componentParameterConfiguration.get(componentParameter3.getMetadataKey());
        assertTrue(fetchedComponentParameter3.isPresent());
        assertEquals(componentParameter3, fetchedComponentParameter3.get());
    }

    @Test
    void componentParameterInsertAlreadyExistsTest() {
        componentParameterConfiguration.insert(componentParameter11);
        assertThrows(MetadataAlreadyExistsException.class,() -> componentParameterConfiguration.insert(componentParameter11));
    }

    @Test
    void componentParameterDeleteTest() {
        componentParameterConfiguration.insert(componentParameter11);
        assertEquals(1, componentParameterConfiguration.getAll().size());

        componentParameterConfiguration.delete(componentParameter11.getMetadataKey());
        assertEquals(0, componentParameterConfiguration.getAll().size());
    }

    @Test
    void componentParameterDeleteMultipleTest() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        assertEquals(2, componentParameterConfiguration.getAll().size());

        componentParameterConfiguration.delete(componentParameter11.getMetadataKey());
        assertEquals(1, componentParameterConfiguration.getAll().size());

        Optional<ComponentParameter> fetchedComponentParameter12 = componentParameterConfiguration.get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals(componentParameter12, fetchedComponentParameter12.get());
    }

    @Test
    void componentParameterDeleteMultipleVersionsTest() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        assertEquals(3, componentParameterConfiguration.getAll().size());

        componentParameterConfiguration.delete(componentParameter11.getMetadataKey());
        assertEquals(2, componentParameterConfiguration.getAll().size());

        Optional<ComponentParameter> fetchedComponentParameter12 = componentParameterConfiguration.get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals(componentParameter12, fetchedComponentParameter12.get());

        Optional<ComponentParameter> fetchedComponentParameter2 = componentParameterConfiguration.get(componentParameter2.getMetadataKey());
        assertTrue(fetchedComponentParameter2.isPresent());
        assertEquals(componentParameter2, fetchedComponentParameter2.get());

    }

    @Test
    void componentParameterDeleteMultipleVersionsAndIdTest() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        componentParameterConfiguration.insert(componentParameter3);
        assertEquals(4, componentParameterConfiguration.getAll().size());

        componentParameterConfiguration.delete(componentParameter2.getMetadataKey());
        assertEquals(3, componentParameterConfiguration.getAll().size());

        Optional<ComponentParameter> fetchedComponentParameter11 = componentParameterConfiguration.get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals(componentParameter11, fetchedComponentParameter11.get());

        Optional<ComponentParameter> fetchedComponentParameter12 = componentParameterConfiguration.get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals(componentParameter12, fetchedComponentParameter12.get());

        Optional<ComponentParameter> fetchedComponentParameter3 = componentParameterConfiguration.get(componentParameter3.getMetadataKey());
        assertTrue(fetchedComponentParameter3.isPresent());
        assertEquals(componentParameter3, fetchedComponentParameter3.get());
    }

    @Test
    void componentParameterDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class,() ->
                componentParameterConfiguration.delete(componentParameter11.getMetadataKey()));
    }

    @Test
    void componentParameterGetNotExistsTest(){
        assertFalse(componentParameterConfiguration.exists(componentParameter11));
        assertFalse(componentParameterConfiguration.get(componentParameter2.getMetadataKey()).isPresent());
    }

    @Test
    void componentParameterUpdateTest() {
        componentParameterConfiguration.insert(componentParameter11);

        Optional<ComponentParameter> fetchedComponentParameter11 = componentParameterConfiguration.get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("value", fetchedComponentParameter11.get().getValue());

        componentParameter11.setValue("dummy");
        componentParameterConfiguration.update(componentParameter11);

        fetchedComponentParameter11 = componentParameterConfiguration.get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("dummy", fetchedComponentParameter11.get().getValue());
    }

    @Test
    void componentParameterUpdateMultipleVersionsTest() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);

        Optional<ComponentParameter> fetchedComponentParameter11 = componentParameterConfiguration.get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("value", fetchedComponentParameter11.get().getValue());

        Optional<ComponentParameter> fetchedComponentParameter12 = componentParameterConfiguration.get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals("value", fetchedComponentParameter12.get().getValue());

        componentParameter11.setValue("dummy");
        componentParameterConfiguration.update(componentParameter11);

        fetchedComponentParameter11 = componentParameterConfiguration.get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("dummy", fetchedComponentParameter11.get().getValue());

        fetchedComponentParameter12 = componentParameterConfiguration.get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals("value", fetchedComponentParameter12.get().getValue());
    }

    @Test
    void componentParameterUpdateMultipleTest() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        componentParameterConfiguration.insert(componentParameter3);

        Optional<ComponentParameter> fetchedComponentParameter11 = componentParameterConfiguration.get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("value", fetchedComponentParameter11.get().getValue());

        Optional<ComponentParameter> fetchedComponentParameter12 = componentParameterConfiguration.get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals("value", fetchedComponentParameter12.get().getValue());

        Optional<ComponentParameter> fetchedComponentParameter2 = componentParameterConfiguration.get(componentParameter2.getMetadataKey());
        assertTrue(fetchedComponentParameter2.isPresent());
        assertEquals("value", fetchedComponentParameter2.get().getValue());

        Optional<ComponentParameter> fetchedComponentParameter3 = componentParameterConfiguration.get(componentParameter3.getMetadataKey());
        assertTrue(fetchedComponentParameter3.isPresent());
        assertEquals("value", fetchedComponentParameter3.get().getValue());

        componentParameter2.setValue("dummy");
        componentParameterConfiguration.update(componentParameter2);

        fetchedComponentParameter11 = componentParameterConfiguration.get(componentParameter11.getMetadataKey());
        assertTrue(fetchedComponentParameter11.isPresent());
        assertEquals("value", fetchedComponentParameter11.get().getValue());

        fetchedComponentParameter12 = componentParameterConfiguration.get(componentParameter12.getMetadataKey());
        assertTrue(fetchedComponentParameter12.isPresent());
        assertEquals("value", fetchedComponentParameter12.get().getValue());

        fetchedComponentParameter2 = componentParameterConfiguration.get(componentParameter2.getMetadataKey());
        assertTrue(fetchedComponentParameter2.isPresent());
        assertEquals("dummy", fetchedComponentParameter2.get().getValue());

        fetchedComponentParameter3 = componentParameterConfiguration.get(componentParameter3.getMetadataKey());
        assertTrue(fetchedComponentParameter3.isPresent());
        assertEquals("value", fetchedComponentParameter3.get().getValue());
    }

    @Test
    void componentParameterGetByComponentTest() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        componentParameterConfiguration.insert(componentParameter3);

        assertEquals(Stream.of(componentParameter11, componentParameter12).collect(Collectors.toList()),
                componentParameterConfiguration.getByComponent(componentParameter11.getMetadataKey().getComponentKey()));
    }

    @Test
    void componentParameterGetByComponent2Test() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        componentParameterConfiguration.insert(componentParameter3);

        assertEquals(Stream.of(componentParameter2).collect(Collectors.toList()),
                componentParameterConfiguration.getByComponent(componentParameter2.getMetadataKey().getComponentKey()));
    }

    @Test
    void componentParameterGetByComponent3Test() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        componentParameterConfiguration.insert(componentParameter3);

        assertEquals(Stream.of(componentParameter3).collect(Collectors.toList()),
                componentParameterConfiguration.getByComponent(componentParameter3.getMetadataKey().getComponentKey()));
    }

    @Test
    void componentParameterDeleteByComponentTest() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        componentParameterConfiguration.insert(componentParameter3);

        assertEquals(4, componentParameterConfiguration.getAll().size());

        componentParameterConfiguration.deleteByComponent(componentParameter11.getMetadataKey().getComponentKey());

        assertEquals(2, componentParameterConfiguration.getAll().size());
    }

    @Test
    void componentParameterDeleteByComponent2Test() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        componentParameterConfiguration.insert(componentParameter3);

        assertEquals(4, componentParameterConfiguration.getAll().size());

        componentParameterConfiguration.deleteByComponent(componentParameter2.getMetadataKey().getComponentKey());

        assertEquals(3, componentParameterConfiguration.getAll().size());
    }

    @Test
    void componentParameterDeleteByComponent3Test() {
        componentParameterConfiguration.insert(componentParameter11);
        componentParameterConfiguration.insert(componentParameter12);
        componentParameterConfiguration.insert(componentParameter2);
        componentParameterConfiguration.insert(componentParameter3);

        assertEquals(4, componentParameterConfiguration.getAll().size());

        componentParameterConfiguration.deleteByComponent(componentParameter3.getMetadataKey().getComponentKey());

        assertEquals(3, componentParameterConfiguration.getAll().size());
    }
}
