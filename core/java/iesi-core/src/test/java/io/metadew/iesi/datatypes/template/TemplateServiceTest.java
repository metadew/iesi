package io.metadew.iesi.datatypes.template;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationKeyValueConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.template.TemplateConfiguration;
import io.metadew.iesi.metadata.configuration.template.matcher.MatcherConfiguration;
import io.metadew.iesi.metadata.configuration.template.matcher.value.MatcherValueConfiguration;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherAnyValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherFixedValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherTemplate;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValueKey;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, MatcherConfiguration.class, MatcherValueConfiguration.class, DataTypeHandler.class, DatasetImplementationHandler.class,
        DatabaseDatasetImplementationKeyValueConfiguration.class})
@ActiveProfiles("test")
class TemplateServiceTest {

    Template template1;
    UUID templateUuid1;

    Template template12;
    UUID templateUuid12;

    Template template2;
    UUID templateUuid2;

    @MockBean
    TemplateConfiguration templateConfiguration;

    @MockBean
    DatasetImplementationHandler datasetImplementationHandler;

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

        templateUuid12 = UUID.randomUUID();
        TemplateKey templateKey12 = TemplateKey.builder()
                .id(templateUuid12)
                .build();
        MatcherKey matcherKey121 = MatcherKey.builder().id(UUID.randomUUID()).build();
        MatcherKey matcherKey122 = MatcherKey.builder().id(UUID.randomUUID()).build();

        template12 = Template.builder()
                .metadataKey(templateKey12)
                .name("template1")
                .version(2L)
                .description("description")
                .matchers(Stream.of(
                        Matcher.builder()
                                .matcherKey(matcherKey121)
                                .key("key1")
                                .templateKey(templateKey12)
                                .matcherValue(MatcherAnyValue.builder()
                                        .matcherValueKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                        .matcherKey(matcherKey121)
                                        .build())
                                .build(),
                        Matcher.builder()
                                .matcherKey(matcherKey122)
                                .key("key2")
                                .templateKey(templateKey12)
                                .matcherValue(MatcherFixedValue.builder()
                                        .metadataKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                        .matcherKey(matcherKey122)
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
    void createTemplateAlreadyExists() {

        when(templateConfiguration.exists(template1.getName(), template1.getVersion()))
                .thenReturn(true);

        assertThatThrownBy(() -> TemplateService.getInstance().insert(template1))
                .hasMessage("Template with name template1 and version 1 already exists");
    }

    @Test
    void updateUnexistingTemplate() {
        MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());

        when(templateConfiguration.exists(template1.getName(), template1.getVersion()))
                .thenReturn(false);

        template1.getMatchers().add(new Matcher(
                matcherKey,
                template1.getMetadataKey(),
                "key4",
                new MatcherTemplate(
                        new MatcherValueKey(UUID.randomUUID()),
                        matcherKey,
                        "template1",
                        2L
                )
        ));

        assertThatThrownBy(() -> TemplateService.getInstance().update(template1))
                .hasMessage("Template with name template1 and version 1 does not exist");
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
        when(templateConfiguration.getByNameAndVersion("template1", 1L))
                .thenReturn(Optional.of(template1));


        assertThat(TemplateService.getInstance().resolve("template1, 1", executionRuntime))
                .isEqualTo(template1);
        assertThatThrownBy(() -> TemplateService.getInstance().resolve("template1, 2", executionRuntime))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> TemplateService.getInstance().resolve("template1", executionRuntime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void matchesSuccessfulTest() {
        templateConfiguration.insert(template1);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);

        DatabaseDatasetImplementation dataset1 = mock(DatabaseDatasetImplementation.class);
        DatabaseDatasetImplementation dataset2 = mock(DatabaseDatasetImplementation.class);

        when(executionRuntime.resolveVariables(anyString()))
                .thenReturn("value2");
        when(executionRuntime.resolveConceptLookup("value2"))
                .thenReturn(new LookupResult("value2", null, null));
        when(templateConfiguration.getByNameAndVersion("template1", 1L))
                .thenReturn(Optional.of(template1));
        when(datasetImplementationHandler.getDataItem(any(), eq("key1"), any(ExecutionRuntime.class)))
                .thenReturn(Optional.of(new Text("test")));
        when(datasetImplementationHandler.getDataItem(any(), eq("key2"), any(ExecutionRuntime.class)))
                .thenReturn(Optional.of(new Text("test")));
        when(datasetImplementationHandler.getDataItem(any(), eq("key3"), any(ExecutionRuntime.class)))
                .thenReturn(Optional.of(new Text("test")));
        when(datasetImplementationHandler.getDataItem(any(), eq("key4"), any(ExecutionRuntime.class)))
                .thenReturn(Optional.of(new Text("test")));


        assertThat(TemplateService.getInstance().matches(dataset1, template1, executionRuntime))
                .isTrue();
        assertThat(TemplateService.getInstance().matches(dataset2, template2, executionRuntime))
                .isTrue();
    }
}
