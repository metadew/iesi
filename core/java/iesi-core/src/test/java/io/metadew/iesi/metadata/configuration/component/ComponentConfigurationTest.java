package io.metadew.iesi.metadata.configuration.component;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.exception.MetadataAlreadyExistsException;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = { ComponentConfiguration.class, ComponentParameterConfiguration.class, ComponentVersionConfiguration.class, ComponentAttributeConfiguration.class } )
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ComponentConfigurationTest {

    private Component component1;
    private Component component2;
    private Component component3;

    @Autowired
    private static MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ComponentConfiguration componentConfiguration;

    @Autowired
    private ComponentVersionConfiguration componentVersionConfiguration;

    @Autowired
    private ComponentAttributeConfiguration componentAttributeConfiguration;

    @Autowired
    private ComponentParameterConfiguration componentParameterConfiguration;

    @BeforeEach
    void setup() {
        SecurityGroupKey securityGroupKey1 = new SecurityGroupKey(UUID.randomUUID());
        component1 = new ComponentBuilder("1", 1)
                .numberOfAttributes(2)
                .numberOfParameters(3)
                .securityGroupKey(securityGroupKey1)
                .securityGroupName("PUBLIC")
                .description("test")
                .name("comp1")
                .build();
        component2 = new ComponentBuilder("1", 2)
                .numberOfAttributes(2)
                .numberOfParameters(3)
                .securityGroupKey(securityGroupKey1)
                .securityGroupName("PUBLIC")
                .name("comp1")
                .description("test")
                .build();
        component3 = new ComponentBuilder("2", 1)
                .numberOfAttributes(2)
                .numberOfParameters(3)
                .securityGroupKey(new SecurityGroupKey(UUID.randomUUID()))
                .securityGroupName("PUBLIC")
                .name("comp2")
                .description("test")
                .build();
    }

    @Test
    void componentNotExistsTest() {
        assertFalse(componentConfiguration.exists(component1));
    }

    @Test
    void componentExistsTest() {
        componentConfiguration.insert(component1);
        assertTrue(componentConfiguration.exists(component1.getMetadataKey()));
    }

    @Test
    void componentInsertTest() {
        componentConfiguration.insert(component1);
        assertEquals(1, componentConfiguration.getAll().size());

        Optional<Component> fetchedComponentVersion1 = componentConfiguration.get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals(component1, fetchedComponentVersion1.get());

    }

    @Test
    void componentInsertAlreadyExistsTest() {
        componentConfiguration.insert(component1);
        assertThrows(MetadataAlreadyExistsException.class, () -> componentConfiguration.insert(component1));
    }

    @Test
    void componentDeleteTest() {
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);
        assertEquals(3, componentConfiguration.getAll().size());

        componentConfiguration.delete(component1.getMetadataKey());

        assertEquals(2, componentConfiguration.getAll().size());
        assertEquals(6, componentParameterConfiguration.getAll().size());
        assertEquals(2, componentVersionConfiguration.getAll().size());
        assertEquals(4, componentAttributeConfiguration.getAll().size());

        Optional<Component> fetchedComponentVersion2 = componentConfiguration.get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals(component2, fetchedComponentVersion2.get());
        Optional<Component> fetchedComponentVersion3 = componentConfiguration.get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals(component3, fetchedComponentVersion3.get());
    }

    @Test
    void componentDeleteDoesNotExistTest() {
        assertThrows(MetadataDoesNotExistException.class, () ->
                componentConfiguration.delete(component1.getMetadataKey()));
    }

    @Test
    void componentGetNotExistsTest() {
        assertFalse(componentConfiguration.exists(component1));
        assertFalse(componentConfiguration.get(component1.getMetadataKey()).isPresent());
    }

    @Test
    void componentGetWithVersionTest() {
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        Optional<Component> fetchedComponentVersion2 = componentConfiguration.getByNameAndVersion("comp1", 1L);
        assertThat(fetchedComponentVersion2).isPresent();
        assertThat(fetchedComponentVersion2.get().getName()).isEqualTo("comp1");
        assertThat(fetchedComponentVersion2.get().getVersion().getMetadataKey().getComponentKey().getVersionNumber()).isEqualTo(1L);
    }

    @Test
    void componentGetWithLatestVersionTest() {
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        Optional<Component> fetchedComponentLatestVersion = componentConfiguration.getByNameAndLatestVersion("comp1");
        assertThat(fetchedComponentLatestVersion).isPresent();
        assertThat(fetchedComponentLatestVersion.get().getName()).isEqualTo("comp1");
        assertThat(fetchedComponentLatestVersion.get().getVersion().getMetadataKey().getComponentKey().getVersionNumber()).isEqualTo(2L);
    }

    @Test
    void componentGetWithUnexistingVersionTest() {
        componentConfiguration.insert(component2);
        assertThat(componentConfiguration.getByNameAndVersion("comp1", 10L)).isEmpty();
    }

    @Test
    void componentGetWithUnexistingNameTest() {
        componentConfiguration.insert(component2);
        assertThat(componentConfiguration.getByNameAndVersion("comp", 2L)).isEmpty();
    }

    @Test
    void componentGetWithUnexistingNameAndVersionTest() {
        assertThat(componentConfiguration.getByNameAndVersion("comp", 2L)).isEmpty();
    }

    @Test
    void componentUpdate1Test() {
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);

        Optional<Component> fetchedComponentVersion1 = componentConfiguration.get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<Component> fetchedComponentVersion2 = componentConfiguration.get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<Component> fetchedComponentVersion3 = componentConfiguration.get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        component1.setDescription("dummy");
        componentConfiguration.update(component1);

        fetchedComponentVersion1 = componentConfiguration.get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("dummy", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = componentConfiguration.get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("dummy", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = componentConfiguration.get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentUpdate2Test() {
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);

        Optional<Component> fetchedComponentVersion1 = componentConfiguration.get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<Component> fetchedComponentVersion2 = componentConfiguration.get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<Component> fetchedComponentVersion3 = componentConfiguration.get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        component2.setDescription("dummy");
        componentConfiguration.update(component2);

        fetchedComponentVersion1 = componentConfiguration.get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("dummy", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = componentConfiguration.get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("dummy", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = componentConfiguration.get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentUpdate3Test() {
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);

        Optional<Component> fetchedComponentVersion1 = componentConfiguration.get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        Optional<Component> fetchedComponentVersion2 = componentConfiguration.get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        Optional<Component> fetchedComponentVersion3 = componentConfiguration.get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("test", fetchedComponentVersion3.get().getDescription());

        component3.setDescription("dummy");
        componentConfiguration.update(component3);

        fetchedComponentVersion1 = componentConfiguration.get(component1.getMetadataKey());
        assertTrue(fetchedComponentVersion1.isPresent());
        assertEquals("test", fetchedComponentVersion1.get().getDescription());
        fetchedComponentVersion2 = componentConfiguration.get(component2.getMetadataKey());
        assertTrue(fetchedComponentVersion2.isPresent());
        assertEquals("test", fetchedComponentVersion2.get().getDescription());
        fetchedComponentVersion3 = componentConfiguration.get(component3.getMetadataKey());
        assertTrue(fetchedComponentVersion3.isPresent());
        assertEquals("dummy", fetchedComponentVersion3.get().getDescription());
    }

    @Test
    void componentUpdateParametersTest() {
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);

        ComponentParameter componentParameter1;
        ComponentParameter componentParameter2;
        ComponentParameter componentParameter3;
        ComponentParameter componentParameter3Bis;
        List<ComponentParameter> componentParameters1 = new ArrayList<>();
        List<ComponentParameter> componentParameters2 = new ArrayList<>();
        List<ComponentParameter> componentParameters3 = new ArrayList<>();
        componentParameter1 = new ComponentParameterBuilder("1", 1, "newParameter1")
                .value("newValue1")
                .build();
        componentParameter2 = new ComponentParameterBuilder("1", 2, "newParameter2")
                .value("newValue2")
                .build();
        componentParameter3 = new ComponentParameterBuilder("2", 1, "newParameter3")
                .value("newValue3")
                .build();
        componentParameter3Bis = new ComponentParameterBuilder("2", 1, "newParameter3Bis")
                .value("newValue3Bis")
                .build();

        componentParameters1.add(componentParameter1);
        componentParameters2.add(componentParameter2);
        componentParameters3.add(componentParameter3);
        componentParameters3.add(componentParameter3Bis);

        component1.setParameters(componentParameters1);
        component2.setParameters(componentParameters2);
        component3.setParameters(componentParameters3);

        componentConfiguration.update(component1);
        componentConfiguration.update(component2);
        componentConfiguration.update(component3);

        assertEquals(componentParameters1, component1.getParameters());
        assertEquals(componentParameters2, component2.getParameters());
        assertEquals(componentParameters3, component3.getParameters());
        assertEquals(2, component3.getParameters().size());
    }

    @Test
    void componentDeleteAllTest() {
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);

        assertEquals(3, componentConfiguration.getAll().size());
        assertEquals(3, componentVersionConfiguration.getAll().size());
        assertEquals(6, componentAttributeConfiguration.getAll().size());
        assertEquals(9, componentParameterConfiguration.getAll().size());

        componentConfiguration.deleteAll();

        assertEquals(0, componentConfiguration.getAll().size());
        assertEquals(0, componentVersionConfiguration.getAll().size());
        assertEquals(0, componentAttributeConfiguration.getAll().size());
        assertEquals(0, componentParameterConfiguration.getAll().size());
    }

    @Test
    void componentGetLatestVersionAllTest() throws MetadataAlreadyExistsException, MetadataDoesNotExistException {
        componentConfiguration.insert(component1);
        componentConfiguration.insert(component2);
        componentConfiguration.insert(component3);

        Optional<Component> fetchedComponent = componentConfiguration.get(component1.getMetadataKey().getId());
        assertTrue(fetchedComponent.isPresent());
        assertEquals(component2, fetchedComponent.get());
    }
}
