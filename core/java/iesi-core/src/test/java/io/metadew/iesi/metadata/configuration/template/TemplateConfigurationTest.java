package io.metadew.iesi.metadata.configuration.template;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, TemplateConfiguration.class, MatcherConfiguration.class, MatcherValueConfiguration.class })
@ActiveProfiles("test")
class TemplateConfigurationTest {

    Template template1;
    UUID templateUuid1;

    Template template2;
    UUID templateUuid2;

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;
    @Autowired
    TemplateConfiguration templateConfiguration;

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
                                .key("a")
                                .templateKey(templateKey1)
                                .matcherValue(MatcherAnyValue.builder()
                                        .matcherValueKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                        .matcherKey(matcherKey11)
                                        .build())
                                .build(),
                        Matcher.builder()
                                .matcherKey(matcherKey12)
                                .key("b")
                                .templateKey(templateKey1)
                                .matcherValue(MatcherFixedValue.builder()
                                        .metadataKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                        .matcherKey(matcherKey12)
                                        .value("value1")
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
                                .key("c")
                                .templateKey(templateKey2)
                                .matcherValue(MatcherAnyValue.builder()
                                        .matcherValueKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                        .matcherKey(matcherKey21)
                                        .build())
                                .build(),
                        Matcher.builder()
                                .matcherKey(matcherKey22)
                                .key("d")
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

        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void testGetAllEmpty() throws SQLException {
        templateConfiguration.getAll();
        assertThat(templateConfiguration.getAll())
                .isEmpty();
    }

    @Test
    void testGetAll() throws SQLException {
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(template1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(template2);
        assertThat(templateConfiguration.getAll())
                .containsOnly(template1, template2);
    }

    @Test
    void testGetByTemplateId() {
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(template1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(template2);

        assertThat(templateConfiguration.get(template1.getMetadataKey()))
                .hasValue(template1);
        assertThat(templateConfiguration.get(template2.getMetadataKey()))
                .hasValue(template2);
    }

    @Test
    void testGetByTemplateIdEmpty() {
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(template1);

        assertThat(templateConfiguration.get(TemplateKey.builder().id(UUID.randomUUID()).build()))
                .isEmpty();
    }

    @Test
    void testInsert() {
        templateConfiguration.insert(template1);

        assertThat(templateConfiguration.get(template1.getMetadataKey()))
                .hasValue(template1);
    }

    @Test
    void testInsertAlreadyExists() {
        templateConfiguration.insert(template1);

        assertThatThrownBy(() -> templateConfiguration.insert(template1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testUpdate() {
        templateConfiguration.insert(template1);
        MatcherKey matcherKey = MatcherKey.builder().id(UUID.randomUUID()).build();
        template1.setMatchers(Stream.of(
                Matcher.builder()
                        .matcherKey(matcherKey)
                        .key("f")
                        .templateKey(template1.getMetadataKey())
                        .matcherValue(MatcherFixedValue.builder()
                                .metadataKey(MatcherValueKey.builder().id(UUID.randomUUID()).build())
                                .matcherKey(matcherKey)
                                .value("test")
                                .build())
                        .build()
        ).collect(Collectors.toList()));
        templateConfiguration.update(template1);

        assertThat(templateConfiguration.get(template1.getMetadataKey()))
                .hasValue(template1);
    }


}
