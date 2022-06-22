package io.metadew.iesi.datatypes.template;

import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherAnyValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherFixedValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherTemplate;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValueKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.reflect.Whitebox;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    private Template template1;
    private UUID templateUuid1;

    private Template template2;
    private UUID templateUuid2;

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
    void initializeTemplates() {
        templateUuid1 = UUID.randomUUID();
        TemplateKey templateKey1 = TemplateKey.builder()
                .id(templateUuid1)
                .build();
        MatcherKey matcherKey11 = MatcherKey.builder().id(UUID.randomUUID()).build();
        MatcherKey matcherKey12 = MatcherKey.builder().id(UUID.randomUUID()).build();
        template1 = Template.builder()
                .metadataKey(templateKey1)
                .name("template1")
                .version(1L)
                .description("description")
                .matchers(Stream.of(
                        Matcher.builder()
                                .matcherKey(matcherKey11)
                                .key("key1")
                                .templateKey(templateKey1)
                                .matcherValue(MatcherAnyValue.builder()
                                        .matcherValueKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                        .matcherKey(matcherKey11)
                                        .build())
                                .build(),
                        Matcher.builder()
                                .matcherKey(matcherKey12)
                                .key("key2")
                                .templateKey(templateKey1)
                                .matcherValue(MatcherFixedValue.builder()
                                        .metadataKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                        .matcherKey(matcherKey12)
                                        .value("value2")
                                        .build())
                                .build()
                ).collect(Collectors.toList()))
                .build();
        templateUuid2 = UUID.randomUUID();
        TemplateKey templateKey2 = TemplateKey.builder()
                .id(templateUuid2)
                .build();
        MatcherKey matcherKey21 = MatcherKey.builder().id(UUID.randomUUID()).build();
        MatcherKey matcherKey22 = MatcherKey.builder().id(UUID.randomUUID()).build();
        template2 = Template.builder()
                .metadataKey(templateKey2)
                .name("template2")
                .version(1L)
                .description("description")
                .matchers(Stream.of(
                        Matcher.builder()
                                .matcherKey(matcherKey21)
                                .key("key3")
                                .templateKey(templateKey2)
                                .matcherValue(MatcherAnyValue.builder()
                                        .matcherValueKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                        .matcherKey(matcherKey21)
                                        .build())
                                .build(),
                        Matcher.builder()
                                .matcherKey(matcherKey22)
                                .key("key4")
                                .templateKey(templateKey2)
                                .matcherValue(MatcherTemplate.builder()
                                        .metadataKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                        .matcherKey(matcherKey22)
                                        .templateName("template1")
                                        .templateVersion(1L)
                                        .build())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }

    @Test
    void equalsTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        when(executionRuntime.resolveVariables("value2"))
                .thenReturn("value2");
        when(executionRuntime.resolveConceptLookup("value2"))
                .thenReturn(new LookupResult("value2", null, null));

        assertThat(TemplateService.getInstance().equals(null, null, executionRuntime))
                .isTrue();
        assertThat(TemplateService.getInstance().equals(template1, null, executionRuntime))
                .isFalse();
        assertThat(TemplateService.getInstance().equals(null, template2, executionRuntime))
                .isFalse();
        assertThat(TemplateService.getInstance().equals(template1, template2, executionRuntime))
                .isFalse();
        assertThat(TemplateService.getInstance().equals(template1, template1, executionRuntime))
                .isTrue();
        assertThat(TemplateService.getInstance().equals(template2, template2, executionRuntime))
                .isTrue();
    }

    @Test
    void resolveTest() {
        MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().save(template1);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        when(executionRuntime.resolveVariables("template1"))
                .thenReturn("template1");
        when(executionRuntime.resolveConceptLookup("template1"))
                .thenReturn(new LookupResult("template1", null, null));
        when(executionRuntime.resolveVariables("1"))
                .thenReturn("1");
        when(executionRuntime.resolveConceptLookup("1"))
                .thenReturn(new LookupResult("1", null, null));
        when(executionRuntime.resolveVariables("2"))
                .thenReturn("2");
        when(executionRuntime.resolveConceptLookup("2"))
                .thenReturn(new LookupResult("2", null, null));


        assertThat(TemplateService.getInstance().resolve("template1, 1", executionRuntime))
                .isEqualTo(template1);
        assertThatThrownBy(() -> TemplateService.getInstance().resolve("template1, 2", executionRuntime))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> TemplateService.getInstance().resolve("template1", executionRuntime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void matchesSuccessfulTest() {
        MetadataRepositoryConfiguration.getInstance().getDesignMetadataRepository().save(template1);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        when(executionRuntime.resolveVariables(anyString()))
                .thenReturn("value2");
        when(executionRuntime.resolveConceptLookup("value2"))
                .thenReturn(new LookupResult("value2", null, null));


        InMemoryDatasetImplementationService datasetHandler = InMemoryDatasetImplementationService.getInstance();
        InMemoryDatasetImplementationService datasetHandlerSpy = Mockito.spy(datasetHandler);
        Whitebox.setInternalState(InMemoryDatasetImplementationService.class, "instance", datasetHandlerSpy);

        InMemoryDatasetImplementation dataset1 = mock(InMemoryDatasetImplementation.class);
        InMemoryDatasetImplementation dataset2 = mock(InMemoryDatasetImplementation.class);
        Mockito
                .doReturn(Optional.of(new Text("test")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key1", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("value2")))
                .when(datasetHandlerSpy).getDataItem(dataset1, "key2", executionRuntime);
        Mockito
                .doReturn(Optional.of(new Text("test")))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key3", executionRuntime);
        Mockito
                .doReturn(Optional.of(dataset1))
                .when(datasetHandlerSpy).getDataItem(dataset2, "key4", executionRuntime);

        assertThat(TemplateService.getInstance().matches(dataset1, template1, executionRuntime))
                .isTrue();
        assertThat(TemplateService.getInstance().matches(dataset2, template2, executionRuntime))
                .isTrue();

        Whitebox.setInternalState(InMemoryDatasetImplementationService.class, "instance", (InMemoryDatasetImplementationService) null);
    }

}
