package io.metadew.iesi.datatypes.template;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.template.TemplateConfiguration;
import io.metadew.iesi.metadata.configuration.template.matcher.MatcherConfiguration;
import io.metadew.iesi.metadata.configuration.template.matcher.value.MatcherValueConfiguration;
import io.metadew.iesi.metadata.definition.template.Template;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherAnyValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValue;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValueKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class, TemplateConfiguration.class, MatcherConfiguration.class, MatcherValueConfiguration.class})
@ActiveProfiles("test")
class TemplateConfigurationTest {

    @Autowired
    MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    TemplateConfiguration templateConfiguration;

    @BeforeEach
    void setUp() {
        metadataRepositoryConfiguration.createAllTables();
    }

    @AfterEach
    void tearDown() {
        metadataRepositoryConfiguration.dropAllTables();
    }

    @Test
    void get() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
            MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
            return new Matcher(
                    matcherKey,
                    templateKey,
                    "key",
                    new MatcherAnyValue(
                            new MatcherValueKey(UUID.randomUUID()),
                            matcherKey
                    ));
        })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        templateConfiguration.insert(template);

        assertThat(templateConfiguration.get(templateKey))
                .hasValue(template);
    }

    @Test
    void getNotFound() {
        assertThat(templateConfiguration.get(new TemplateKey(UUID.randomUUID())))
                .isEmpty();
    }

    @Test
    void exists() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        templateConfiguration.insert(template);

        assertThat(templateConfiguration.exists("template", 1L))
                .isTrue();
    }

    @Test
    void notExist() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        templateConfiguration.insert(template);

        assertThat(templateConfiguration.exists("template", 2L))
                .isFalse();
    }

    @Test
    void getByNameAndVersion() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        templateConfiguration.insert(template);

        assertThat(templateConfiguration.getByNameAndVersion("template", 1L))
                .hasValue(template);
    }
    @Test
    void getByNameAndVersionNotFound() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        templateConfiguration.insert(template);

        assertThat(templateConfiguration.getByNameAndVersion("template", 2L))
                .isEmpty();
    }

    @Test
    void getAll() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());
        TemplateKey templateKey2 = new TemplateKey(UUID.randomUUID());
        TemplateKey templateKey3 = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        List<Matcher> matchers2 = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey2,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        List<Matcher> matchers3 = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey3,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        Template template2 = new Template(
                templateKey2,
                "template2",
                1L,
                "description",
                matchers2);

        Template template3 = new Template(
                templateKey3,
                "template3",
                1L,
                "description",
                matchers3);

        templateConfiguration.insert(template);
        templateConfiguration.insert(template2);
        templateConfiguration.insert(template3);

        assertThat(templateConfiguration.getAll())
                .contains(template, template2, template3);
    }

    @Test
    void delete() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        templateConfiguration.insert(template);

        assertThat(templateConfiguration.getByNameAndVersion("template", 1L))
                .isNotEmpty();

        assertThatCode(() -> templateConfiguration.delete(templateKey))
                .doesNotThrowAnyException();
    }
    @Test
    void deleteNotExist() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());
        assertThatCode(() -> templateConfiguration.delete(templateKey))
                .doesNotThrowAnyException();
    }

    @Test
    void deleteByNameAndVersion() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());
        TemplateKey templateKey2 = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        List<Matcher> matchers2 = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey2,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        Template template2 = new Template(
                templateKey2,
                "template",
                2L,
                "description",
                matchers2);

        templateConfiguration.insert(template);
        templateConfiguration.insert(template2);

        assertThat(templateConfiguration.getByNameAndVersion("template", 1L))
                .isNotEmpty();
        assertThat(templateConfiguration.getByNameAndVersion("template", 2L))
                .isNotEmpty();

        assertThatCode(() -> templateConfiguration.deleteByNameAndVersion("template", 1L))
                .doesNotThrowAnyException();
        assertThat(templateConfiguration.getByNameAndVersion("template", 1L))
                .isEmpty();
        assertThat(templateConfiguration.getByNameAndVersion("template", 2L))
                .isNotEmpty();
    }

    @Test
    void insert() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        assertThatCode(() -> templateConfiguration.insert(template))
                .doesNotThrowAnyException();
        assertThat(templateConfiguration.get(templateKey))
                .hasValue(template);
    }
    @Test
    void insertSameNameDifferentVersion() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());
        TemplateKey templateKey2 = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        List<Matcher> matchers2 = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey2,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        Template template2 = new Template(
                templateKey2,
                "template",
                2L,
                "description",
                matchers2);

        assertThatCode(() -> templateConfiguration.insert(template))
                .doesNotThrowAnyException();
        assertThatCode(() -> templateConfiguration.insert(template2))
                .doesNotThrowAnyException();
        assertThat(templateConfiguration.get(templateKey))
                .hasValue(template);
        assertThat(templateConfiguration.get(templateKey2))
                .hasValue(template2);
    }


    @Test
    void insertAlreadyExists() {
        TemplateKey templateKey = new TemplateKey(UUID.randomUUID());

        List<Matcher> matchers = Stream.generate(() -> {
                    MatcherKey matcherKey = new MatcherKey(UUID.randomUUID());
                    return new Matcher(
                            matcherKey,
                            templateKey,
                            "key",
                            new MatcherAnyValue(
                                    new MatcherValueKey(UUID.randomUUID()),
                                    matcherKey
                            ));
                })
                .limit(1)
                .collect(Collectors.toList());

        Template template = new Template(
                templateKey,
                "template",
                1L,
                "description",
                matchers);

        templateConfiguration.insert(template);

        assertThatThrownBy(() -> templateConfiguration.insert(template))
                .hasMessageContaining("Unique index or primary key violation");
    }


}
