package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
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

@SpringBootTest(classes = ComponentVersionConfiguration.class)
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ComponentVersionConfigurationTest {

    private ComponentVersion componentVersion1;
    private ComponentVersion componentVersion2;
    private ComponentVersion componentVersion3;

    @Autowired
    private ComponentVersionConfiguration componentVersionConfiguration;

    @BeforeEach
    void setup() {
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
        assertFalse(componentVersionConfiguration.exists(componentVersion1));
    }

    @Test
    void componentVersionExistsTest() {
        componentVersionConfiguration.insert(componentVersion1);
        assertTrue(componentVersionConfiguration.exists(componentVersion1.getMetadataKey()));
    }

    @Test
    void componentVersionInsertTest() {
        componentVersionConfiguration.insert(componentVersion1);
        assertEquals(1, componentVersionConfiguration.getAll().size());

        Optional<ComponentVersion> fetchedComponentVersion1 = componentVersionConfiguration.get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals(componentVersion1, fetchedComponentVersion1.get());

    }

    @Test
    void componentVersionInsertAlreadyExistsTest() {
        componentVersionConfiguration.insert(componentVersion1);
        assertThrows(MetadataAlreadyExistsException.class, () -> componentVersionConfiguration.insert(componentVersion1));
    }

    @Test
    void componentVersionDeleteTest() {
        componentVersionConfiguration.insert(componentVersion1);
        componentVersionConfiguration.insert(componentVersion2);
        componentVersionConfiguration.insert(componentVersion3);
        assertEquals(3, componentVersionConfiguration.getAll().size());

        componentVersionConfiguration.delete(componentVersion1.getMetadataKey());

        assertEquals(2, componentVersionConfiguration.getAll().size());

        Optional<ComponentVersion> fetchedComponentVersion2 = componentVersionConfiguration.get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals(componentVersion2, fetchedComponentVersion2.get());
        Optional<ComponentVersion> fetchedComponentVersion3 = componentVersionConfiguration.get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals(componentVersion3, fetchedComponentVersion3.get());
    }

    @Test
    void componentVersionDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () ->
                componentVersionConfiguration.delete(componentVersion1.getMetadataKey()));
    }

    @Test
    void componentVersionGetNotExistsTest() {
        assertFalse(componentVersionConfiguration.exists(componentVersion1));
        assertFalse(componentVersionConfiguration.get(componentVersion1.getMetadataKey()).isPresent());
    }

    @Test
    void componentVersionUpdate1Test() {
        componentVersionConfiguration.insert(componentVersion1);
        componentVersionConfiguration.insert(componentVersion2);
        componentVersionConfiguration.insert(componentVersion3);

        Optional<ComponentVersion> fetchedComponentVersion1 = componentVersionConfiguration.get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion2 = componentVersionConfiguration.get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion3 = componentVersionConfiguration.get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        componentVersion1.setDescription("dummy");
        componentVersionConfiguration.update(componentVersion1);

        fetchedComponentVersion1 = componentVersionConfiguration.get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("dummy", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = componentVersionConfiguration.get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = componentVersionConfiguration.get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentVersionUpdate2Test() {
        componentVersionConfiguration.insert(componentVersion1);
        componentVersionConfiguration.insert(componentVersion2);
        componentVersionConfiguration.insert(componentVersion3);

        Optional<ComponentVersion> fetchedComponentVersion1 = componentVersionConfiguration.get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion2 = componentVersionConfiguration.get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion3 = componentVersionConfiguration.get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        componentVersion2.setDescription("dummy");
        componentVersionConfiguration.update(componentVersion2);

        fetchedComponentVersion1 = componentVersionConfiguration.get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = componentVersionConfiguration.get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("dummy", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = componentVersionConfiguration.get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentVersionUpdate3Test() {
        componentVersionConfiguration.insert(componentVersion1);
        componentVersionConfiguration.insert(componentVersion2);
        componentVersionConfiguration.insert(componentVersion3);

        Optional<ComponentVersion> fetchedComponentVersion1 = componentVersionConfiguration.get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion2 = componentVersionConfiguration.get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<ComponentVersion> fetchedComponentVersion3 = componentVersionConfiguration.get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        componentVersion3.setDescription("dummy");
        componentVersionConfiguration.update(componentVersion3);

        fetchedComponentVersion1 = componentVersionConfiguration.get(componentVersion1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = componentVersionConfiguration.get(componentVersion2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = componentVersionConfiguration.get(componentVersion3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("dummy", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentVersionGetByComponentId1Test() {
        componentVersionConfiguration.insert(componentVersion1);
        componentVersionConfiguration.insert(componentVersion2);
        componentVersionConfiguration.insert(componentVersion3);

        assertEquals(Stream.of(componentVersion1, componentVersion2).collect(Collectors.toList()),
                componentVersionConfiguration.getByComponent(componentVersion1.getMetadataKey().getComponentKey().getId()));
    }

    @Test
    void componentVersionGetByComponentId2Test() {
        componentVersionConfiguration.insert(componentVersion1);
        componentVersionConfiguration.insert(componentVersion2);
        componentVersionConfiguration.insert(componentVersion3);

        assertEquals(Stream.of(componentVersion3).collect(Collectors.toList()),
                componentVersionConfiguration.getByComponent(componentVersion3.getMetadataKey().getComponentKey().getId()));
    }

    @Test
    void componentVersionDeleteByComponentId1Test() {
        componentVersionConfiguration.insert(componentVersion1);
        componentVersionConfiguration.insert(componentVersion2);
        componentVersionConfiguration.insert(componentVersion3);

        componentVersionConfiguration.deleteByComponentId(componentVersion3.getMetadataKey().getComponentKey().getId());

        assertEquals(Stream.of(componentVersion1, componentVersion2).collect(Collectors.toList()),
                componentVersionConfiguration.getByComponent(componentVersion1.getMetadataKey().getComponentKey().getId()));
    }

    @Test
    void componentVersionDeleteByComponentId2Test() {
        componentVersionConfiguration.insert(componentVersion1);
        componentVersionConfiguration.insert(componentVersion2);
        componentVersionConfiguration.insert(componentVersion3);

        componentVersionConfiguration.deleteByComponentId(componentVersion1.getMetadataKey().getComponentKey().getId());

        assertEquals(Stream.of(componentVersion3).collect(Collectors.toList()),
                componentVersionConfiguration.getByComponent(componentVersion3.getMetadataKey().getComponentKey().getId()));
    }

    @Test
    void componentVersionGetLatestVersionByComponentIdTest() throws MetadataAlreadyExistsException, MetadataDoesNotExistException {
        componentVersionConfiguration.insert(componentVersion1);
        componentVersionConfiguration.insert(componentVersion2);
        componentVersionConfiguration.insert(componentVersion3);

        Optional<ComponentVersion> fetchedComponentVersion2 = componentVersionConfiguration.getLatestVersionByComponentId(componentVersion1.getMetadataKey().getComponentKey().getId());
        Optional<ComponentVersion> fetchedComponentVersion3 = componentVersionConfiguration.getLatestVersionByComponentId(componentVersion3.getMetadataKey().getComponentKey().getId());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals(2, fetchedComponentVersion2.get().getMetadataKey().getComponentKey().getVersionNumber());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals(1, fetchedComponentVersion3.get().getMetadataKey().getComponentKey().getVersionNumber());
    }

}
