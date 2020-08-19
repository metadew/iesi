package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import io.metadew.iesi.metadata.definition.script.ScriptVersion;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptLabelKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptVersionKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.script.ScriptFilter;
import io.metadew.iesi.server.rest.script.ScriptFilterOption;
import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.action.ActionParameterDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
class ScriptDtoRepositoryTest {

    @Autowired
    private IScriptDtoRepository scriptDtoRepository;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ScriptResultConfiguration scriptResultConfiguration;

    @Autowired
    private ScriptConfiguration scriptConfiguration;

    @Autowired
    private ScriptDtoModelAssembler scriptDtoModelAssembler;

    @BeforeEach
    void setup() {
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
    }

    @Test
    void getAllNoResultsTest() {
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoRepository.getAll(pageable, new ArrayList<>(), false, new ArrayList<>()))
                .isEmpty();
    }

    @Test
    void getAllTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script2Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoRepository.getAll(pageable, new ArrayList<>(), false, new ArrayList<>()))
                .containsOnly(script1Dto, script2Dto);
    }

    @Test
    void getAllPaginatedAllInclusiveTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script2Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        Pageable pageable = PageRequest.of(0, 2);
        assertThat(scriptDtoRepository.getAll(pageable, new ArrayList<>(), false, new ArrayList<>()))
                .containsOnly(script1Dto, script2Dto);
    }

    @Test
    void getAllPaginatedSomeInclusiveTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        Pageable pageable = PageRequest.of(0, 1);
        assertThat(scriptDtoRepository.getAll(pageable, new ArrayList<>(), false, new ArrayList<>()))
                .containsOnly(script1Dto);
    }

    @Test
    void getAllPaginatedSortedAscTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script2Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"));
        assertThat(scriptDtoRepository.getAll(pageable, new ArrayList<>(), false, new ArrayList<>()))
                .containsExactly(script1Dto, script2Dto);
    }

    @Test
    void getAllPaginatedSortedDescTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script2Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "name"));
        assertThat(scriptDtoRepository.getAll(pageable, new ArrayList<>(), false, new ArrayList<>()))
                .containsExactly(script2Dto, script1Dto);
    }

    @Test
    void getAllFilteredOnNameTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script2Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        assertThat(scriptDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>(), false, Stream.of(new ScriptFilter(ScriptFilterOption.NAME, "ript", false)).collect(Collectors.toList())))
                .containsOnly(script2Dto, script1Dto);
        assertThat(scriptDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>(), false, Stream.of(new ScriptFilter(ScriptFilterOption.NAME, "ript1", false)).collect(Collectors.toList())))
                .containsExactly(script1Dto);
        assertThat(scriptDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>(), false, Stream.of(new ScriptFilter(ScriptFilterOption.NAME, "ript2", false)).collect(Collectors.toList())))
                .containsExactly(script2Dto);
    }

    @Test
    void getAllFilteredOnLabelTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label2")
                                .value("value2a")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label3")
                                .value("value3")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script2Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label2", "value2a"),
                        new ScriptLabelDto("label3", "value3"))
                        .collect(Collectors.toSet()))
                .build();
        assertThat(scriptDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>(), false, Stream.of(new ScriptFilter(ScriptFilterOption.LABEL, "label1:value", false)).collect(Collectors.toList())))
                .containsOnly(script1Dto);
        assertThat(scriptDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>(), false, Stream.of(new ScriptFilter(ScriptFilterOption.LABEL, "label2:value2", false)).collect(Collectors.toList())))
                .containsOnly(script1Dto, script2Dto);
        assertThat(scriptDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>(), false, Stream.of(new ScriptFilter(ScriptFilterOption.LABEL, "label2:value2a", false)).collect(Collectors.toList())))
                .containsOnly(script2Dto);
        assertThat(scriptDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>(), false, Stream.of(new ScriptFilter(ScriptFilterOption.LABEL, "label3:value3", false)).collect(Collectors.toList())))
                .containsExactly(script2Dto);
    }

    @Test
    void getAllLatestVersionTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 2L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 2L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label2")
                                .value("value2a")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label3")
                                .value("value3")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        ScriptDto script2Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(2L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label2", "value2a"),
                        new ScriptLabelDto("label3", "value3"))
                        .collect(Collectors.toSet()))
                .build();
        assertThat(scriptDtoRepository.getAll(Pageable.unpaged(), new ArrayList<>(), true, new ArrayList<>()))
                .containsOnly(script2Dto);
    }

    @Test
    void getAllAllCombinationsTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("scriptB")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 2L))
                .name("scriptB")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 2L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 2L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label2")
                                .value("value2a")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label3")
                                .value("value3")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script3Uuid = UUID.randomUUID();
        UUID script3Action1Uuid = UUID.randomUUID();
        UUID script3Action2Uuid = UUID.randomUUID();
        UUID script3Label1Uuid = UUID.randomUUID();
        UUID script3Label2Uuid = UUID.randomUUID();
        Script script3 = Script.builder()
                .scriptKey(new ScriptKey(script3Uuid.toString(), 2L))
                .name("scriptA")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script3Uuid.toString(), 2L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script3Uuid.toString(), 2L), script3Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script3Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script3Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script3Uuid.toString(), 2L), script3Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script3Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script3Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script3Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label1Uuid.toString()))
                                .name("label2")
                                .value("value2a")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script3Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label2Uuid.toString()))
                                .name("label3")
                                .value("value3")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script4Uuid = UUID.randomUUID();
        UUID script4Action1Uuid = UUID.randomUUID();
        UUID script4Action2Uuid = UUID.randomUUID();
        UUID script4Label1Uuid = UUID.randomUUID();
        UUID script4Label2Uuid = UUID.randomUUID();
        Script script4 = Script.builder()
                .scriptKey(new ScriptKey(script4Uuid.toString(), 3L))
                .name("scriptBA")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script4Uuid.toString(), 3L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script4Uuid.toString(), 3L), script4Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script4Uuid.toString(), 3L), script4Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script4Uuid.toString(), 3L), script4Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script4Uuid.toString(), 3L), script4Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script4Uuid.toString(), 3L), script4Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script4Uuid.toString(), 3L), script4Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script4Uuid.toString(), 3L))
                                .scriptLabelKey(new ScriptLabelKey(script4Label1Uuid.toString()))
                                .name("label2")
                                .value("value2a")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script4Uuid.toString(), 3L))
                                .scriptLabelKey(new ScriptLabelKey(script4Label2Uuid.toString()))
                                .name("label3")
                                .value("value3")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        scriptConfiguration.insert(script3);
        scriptConfiguration.insert(script4);
        ScriptDto script2Dto = ScriptDto.builder()
                .name("scriptB")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(2L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label2", "value2a"),
                        new ScriptLabelDto("label3", "value3"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script4Dto = ScriptDto.builder()
                .name("scriptBA")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(3L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label2", "value2a"),
                        new ScriptLabelDto("label3", "value3"))
                        .collect(Collectors.toSet()))
                .build();
        System.out.println(scriptDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name")),
                new ArrayList<>(),
                true,
                Stream.of(new ScriptFilter(ScriptFilterOption.NAME, "criptB", false), new ScriptFilter(ScriptFilterOption.LABEL, "label2:ue2", false)).collect(Collectors.toList())).getContent());
        assertThat(scriptDtoRepository.getAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name")),
                new ArrayList<>(),
                true,
                Stream.of(new ScriptFilter(ScriptFilterOption.NAME, "criptB", false), new ScriptFilter(ScriptFilterOption.LABEL, "label2:ue2", false)).collect(Collectors.toList())))
                .containsExactly(script2Dto, script4Dto);
        assertThat(scriptDtoRepository.getAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "name")),
                new ArrayList<>(),
                true,
                Stream.of(new ScriptFilter(ScriptFilterOption.NAME, "criptB", false), new ScriptFilter(ScriptFilterOption.LABEL, "label2:ue2", false)).collect(Collectors.toList())))
                .containsExactly(script2Dto);
    }

    @Test
    void getByNameTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script3Action1Uuid = UUID.randomUUID();
        UUID script3Action2Uuid = UUID.randomUUID();
        UUID script3Label1Uuid = UUID.randomUUID();
        UUID script3Label2Uuid = UUID.randomUUID();
        Script script3 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 2L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        scriptConfiguration.insert(script3);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script2Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script3Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(2L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoRepository.getByName(pageable, "script1", new ArrayList<>(), false))
                .containsOnly(script1Dto);
        assertThat(scriptDtoRepository.getByName(pageable, "script2", new ArrayList<>(), false))
                .containsOnly(script2Dto, script3Dto);
    }

    @Test
    void getByNameNoResultsTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoRepository.getByName(pageable, "script3", new ArrayList<>(), false))
                .isEmpty();
    }

    @Test
    void getByNameLatestVersionTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script3Action1Uuid = UUID.randomUUID();
        UUID script3Action2Uuid = UUID.randomUUID();
        UUID script3Label1Uuid = UUID.randomUUID();
        UUID script3Label2Uuid = UUID.randomUUID();
        Script script3 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 2L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        scriptConfiguration.insert(script3);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script3Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(2L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoRepository.getByName(pageable, "script1", new ArrayList<>(), true))
                .containsOnly(script1Dto);
        assertThat(scriptDtoRepository.getByName(pageable, "script2", new ArrayList<>(), true))
                .containsOnly(script3Dto);
    }

    @Test
    void getByNameAndVersionTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script3Action1Uuid = UUID.randomUUID();
        UUID script3Action2Uuid = UUID.randomUUID();
        UUID script3Label1Uuid = UUID.randomUUID();
        UUID script3Label2Uuid = UUID.randomUUID();
        Script script3 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 2L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        scriptConfiguration.insert(script3);
        ScriptDto script1Dto = ScriptDto.builder()
                .name("script1")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(1L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        ScriptDto script3Dto = ScriptDto.builder()
                .name("script2")
                .description("script description")
                .parameters(new HashSet<>())
                .version(new ScriptVersionDto(2L, "version description"))
                .actions(Stream.of(
                        ActionDto.builder()
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries(4)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build(),
                        ActionDto.builder()
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected(true)
                                .errorStop(false)
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries(2)
                                .parameters(Stream.of(
                                        new ActionParameterDto("parameter 1", "value1"),
                                        new ActionParameterDto("parameter 2", "value2"))
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toSet()))
                .labels(Stream.of(
                        new ScriptLabelDto("label1", "value1"),
                        new ScriptLabelDto("label2", "value2"))
                        .collect(Collectors.toSet()))
                .build();
        assertThat(scriptDtoRepository.getByNameAndVersion("script1", 1L, new ArrayList<>()))
                .hasValue(script1Dto);
        assertThat(scriptDtoRepository.getByNameAndVersion("script2", 2L, new ArrayList<>()))
                .hasValue(script3Dto);
    }

    @Test
    void getByNameAndVersionNoResultsTest() {
        UUID script1Uuid = UUID.randomUUID();
        UUID script1Action1Uuid = UUID.randomUUID();
        UUID script1Action2Uuid = UUID.randomUUID();
        UUID script1Label1Uuid = UUID.randomUUID();
        UUID script1Label2Uuid = UUID.randomUUID();
        Script script1 = Script.builder()
                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                .name("script1")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script1Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script1Uuid.toString(), 1L), script1Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script1Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script1Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script2Uuid = UUID.randomUUID();
        UUID script2Action1Uuid = UUID.randomUUID();
        UUID script2Action2Uuid = UUID.randomUUID();
        UUID script2Label1Uuid = UUID.randomUUID();
        UUID script2Label2Uuid = UUID.randomUUID();
        Script script2 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 1L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 1L), script2Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 1L))
                                .scriptLabelKey(new ScriptLabelKey(script2Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        UUID script3Action1Uuid = UUID.randomUUID();
        UUID script3Action2Uuid = UUID.randomUUID();
        UUID script3Label1Uuid = UUID.randomUUID();
        UUID script3Label2Uuid = UUID.randomUUID();
        Script script3 = Script.builder()
                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                .name("script2")
                .description("script description")
                .version(ScriptVersion.builder()
                        .scriptVersionKey(new ScriptVersionKey(new ScriptKey(script2Uuid.toString(), 2L)))
                        .description("version description")
                        .build())
                .parameters(new ArrayList<>())
                .actions(Stream.of(
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()))
                                .name("action1")
                                .number(1L)
                                .description("Action 1")
                                .component("component 1")
                                .condition("condition 1")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 1")
                                .iteration("iteration 1")
                                .retries("4")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action1Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build(),
                        Action.builder()
                                .actionKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()))
                                .name("action2")
                                .number(2L)
                                .description("Action 2")
                                .component("component 2")
                                .condition("condition 2")
                                .errorExpected("y")
                                .errorStop("n")
                                .type("type 2")
                                .iteration("iteration 2")
                                .retries("2")
                                .parameters(Stream.of(
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 1"))
                                                .value("value1")
                                                .build(),
                                        ActionParameter.builder()
                                                .actionParameterKey(new ActionParameterKey(new ActionKey(new ScriptKey(script2Uuid.toString(), 2L), script3Action2Uuid.toString()), "parameter 2"))
                                                .value("value2")
                                                .build()
                                )
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .labels(Stream.of(
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label1Uuid.toString()))
                                .name("label1")
                                .value("value1")
                                .build(),
                        ScriptLabel.builder()
                                .scriptKey(new ScriptKey(script2Uuid.toString(), 2L))
                                .scriptLabelKey(new ScriptLabelKey(script3Label2Uuid.toString()))
                                .name("label2")
                                .value("value2")
                                .build())
                        .collect(Collectors.toList()))
                .build();
        scriptConfiguration.insert(script1);
        scriptConfiguration.insert(script2);
        scriptConfiguration.insert(script3);
        assertThat(scriptDtoRepository.getByNameAndVersion("script3", 1L, new ArrayList<>()))
                .isEmpty();
        assertThat(scriptDtoRepository.getByNameAndVersion("script2", 3L, new ArrayList<>()))
                .isEmpty();
    }

}
