package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import io.metadew.iesi.server.rest.Application;
import io.metadew.iesi.server.rest.builder.script.ScriptBuilder;
import io.metadew.iesi.server.rest.builder.scriptresult.ScriptResultBuilder;
import io.metadew.iesi.server.rest.configuration.TestConfiguration;
import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.action.ActionParameterDto;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptExecutionDto;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptExecutionInformation;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {"spring.main.allow-bean-definition-overriding=true"})
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("test")
class ScriptDtoServiceTest {

    @Autowired
    private IScriptDtoService scriptDtoService;

    @Autowired
    private MetadataRepositoryConfiguration metadataRepositoryConfiguration;

    @Autowired
    private ScriptResultConfiguration scriptResultConfiguration;

    @BeforeEach
    void setup() {
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
        //metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::dropAllTables);
        //metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::createAllTables);
    }

    @Test
    void getAllNoScriptsTest() {
        assertEquals(0, scriptDtoService.getAll().size());
    }

    @Test
    void getAllSimpleTest() {
        ScriptResult scriptResult = new ScriptResult(new ScriptResultKey("123", 1L), 1L, " ", "", 1L, "", ScriptRunStatus.SUCCESS, LocalDateTime.now(), LocalDateTime.now());
        ScriptResult scriptResult1 = ScriptResult.builder().scriptResultKey(
                ScriptResultKey.builder()
                        .runId("azeaz")
                        .processId(1L).build())
                .scriptId("azraze")
                .build();

        scriptResultConfiguration.insert(scriptResult);


        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        assertEquals(1, scriptDtoService.getAll().size());
        assertEquals(new ScriptDto("script0", "dummy script",
                        new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                        Stream.of(
                                new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())),
                                new ActionDto(1, "action1", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList()))
                        ).collect(Collectors.toList()),
                        Stream.of(
                                new ScriptLabelDto("label0", "value0"),
                                new ScriptLabelDto("label1", "value1")
                        ).collect(Collectors.toList()), null, null),
                scriptDtoService.getAll().get(0));
    }

    @Test
    void getAllMultipleScriptsTest() {
        Script script1 = ScriptBuilder.simpleScript("script0", 0, 1, 2, 1);
        Script script2 = ScriptBuilder.simpleScript("script1", 0, 1, 2, 1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        assertEquals(2, scriptDtoService.getAll().size());
        assertThat(scriptDtoService.getAll(),
                CoreMatchers.hasItems(new ScriptDto("script0", "dummy script",
                                new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                                Stream.of(new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())))
                                        .collect(Collectors.toList()),
                                Stream.of(new ScriptLabelDto("label0", "value0"))
                                        .collect(Collectors.toList()), null, null),
                        new ScriptDto("script1", "dummy script",
                                new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                                Stream.of(new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())))
                                        .collect(Collectors.toList()),
                                Stream.of(new ScriptLabelDto("label0", "value0"))
                                        .collect(Collectors.toList()), null, null)));
    }

    @Test
    void getAllMultipleScriptVersionsTest() {
        Script script1 = ScriptBuilder.simpleScript("script0", 0, 1, 2, 1);
        Script script2 = ScriptBuilder.simpleScript("script0", 1, 1, 2, 1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        assertEquals(2, scriptDtoService.getAll().size());
        assertThat(scriptDtoService.getAll(),
                CoreMatchers.hasItems(new ScriptDto("script0", "dummy script",
                                new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                                Stream.of(new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())))
                                        .collect(Collectors.toList()),
                                Stream.of(new ScriptLabelDto("label0", "value0"))
                                        .collect(Collectors.toList()), null, null),
                        new ScriptDto("script0", "dummy script",
                                new ScriptVersionDto(1, "dummy version"), new ArrayList<>(),
                                Stream.of(new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())))
                                        .collect(Collectors.toList()),
                                Stream.of(new ScriptLabelDto("label0", "value0"))
                                        .collect(Collectors.toList()), null, null)));
    }

    @Test
    void getAllSimpleWithSingleExecutionExecutionExpansionEnabledTest() {
        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        String runId = UUID.randomUUID().toString();
        ScriptResult scriptResult = new ScriptResultBuilder(runId, 0)
                .scriptName("script0")
                .scriptVersion(0L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:10"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:20"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult);

        assertEquals(1, scriptDtoService.getAll(Stream.of("execution").collect(Collectors.toList())).size());
        assertEquals(new ScriptDto("script0", "dummy script",
                        new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                        Stream.of(
                                new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())),
                                new ActionDto(1, "action1", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList()))
                        ).collect(Collectors.toList()),
                        Stream.of(
                                new ScriptLabelDto("label0", "value0"),
                                new ScriptLabelDto("label1", "value1")
                        ).collect(Collectors.toList()),
                        new ScriptExecutionInformation(null, Stream.of(
                                new ScriptExecutionDto(
                                        runId,
                                        "test",
                                        ScriptRunStatus.SUCCESS,
                                        LocalDateTime.parse("2020-05-20T10:10:10"),
                                        LocalDateTime.parse("2020-05-20T10:10:20"))
                        ).collect(Collectors.toList())), null),
                scriptDtoService.getAll(Stream.of("execution").collect(Collectors.toList())).get(0));
    }

    @Test
    void getAllSimpleWithMultipleExecutionExecutionExpansionEnabledTest() {
        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        String runId1 = UUID.randomUUID().toString();
        ScriptResult scriptResult1 = new ScriptResultBuilder(runId1, 0)
                .scriptName("script0")
                .scriptVersion(0L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:10"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:20"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult1);
        String runId2 = UUID.randomUUID().toString();
        ScriptResult scriptResult2 = new ScriptResultBuilder(runId2, 0)
                .scriptName("script0")
                .scriptVersion(0L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.ERROR)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:05"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:15"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult2);

        assertEquals(1, scriptDtoService.getAll(Stream.of("execution").collect(Collectors.toList())).size());
        assertEquals(new ScriptDto("script0", "dummy script",
                        new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                        Stream.of(
                                new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())),
                                new ActionDto(1, "action1", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList()))
                        ).collect(Collectors.toList()),
                        Stream.of(
                                new ScriptLabelDto("label0", "value0"),
                                new ScriptLabelDto("label1", "value1")
                        ).collect(Collectors.toList()),
                        new ScriptExecutionInformation(null, Stream.of(
                                new ScriptExecutionDto(
                                        runId1,
                                        "test",
                                        ScriptRunStatus.SUCCESS,
                                        LocalDateTime.parse("2020-05-20T10:10:10"),
                                        LocalDateTime.parse("2020-05-20T10:10:20"))
                        ).collect(Collectors.toList())), null),
                scriptDtoService.getAll(Stream.of("execution").collect(Collectors.toList())).get(0));
    }


    @Test
    void getAllSimpleWithSingleExecutionExecutionExpansionDisabledTest() {
        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        String runId = UUID.randomUUID().toString();
        ScriptResult scriptResult = new ScriptResultBuilder(runId, 0)
                .scriptName("script0")
                .scriptVersion(0L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:10"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:20"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult);

        assertEquals(1, scriptDtoService.getAll().size());
        assertEquals(new ScriptDto("script0", "dummy script",
                        new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                        Stream.of(
                                new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())),
                                new ActionDto(1, "action1", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList()))
                        ).collect(Collectors.toList()),
                        Stream.of(
                                new ScriptLabelDto("label0", "value0"),
                                new ScriptLabelDto("label1", "value1")
                        ).collect(Collectors.toList()),
                        null, null),
                scriptDtoService.getAll().get(0));
    }

    @Test
    void getByNameSimpleTest() {
        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        assertEquals(1, scriptDtoService.getByName("script0").size());
        assertEquals(new ScriptDto("script0", "dummy script",
                        new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                        Stream.of(
                                new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())),
                                new ActionDto(1, "action1", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList()))
                        ).collect(Collectors.toList()),
                        Stream.of(
                                new ScriptLabelDto("label0", "value0"),
                                new ScriptLabelDto("label1", "value1")
                        ).collect(Collectors.toList()), null, null),
                scriptDtoService.getByName("script0").get(0));
    }

    @Test
    void getByNameSimpleNonExistentTest() {
        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        assertEquals(0, scriptDtoService.getByName("script1").size());
    }


    @Test
    void getByNameMultipleScriptVersionsTest() {
        Script script1 = ScriptBuilder.simpleScript("script0", 0, 1, 2, 1);
        Script script2 = ScriptBuilder.simpleScript("script0", 1, 1, 2, 1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        assertEquals(2, scriptDtoService.getByName("script0").size());
        assertThat(scriptDtoService.getByName("script0"),
                CoreMatchers.hasItems(new ScriptDto("script0", "dummy script",
                                new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                                Stream.of(new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())))
                                        .collect(Collectors.toList()),
                                Stream.of(new ScriptLabelDto("label0", "value0"))
                                        .collect(Collectors.toList()), null, null),
                        new ScriptDto("script0", "dummy script",
                                new ScriptVersionDto(1, "dummy version"), new ArrayList<>(),
                                Stream.of(new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())))
                                        .collect(Collectors.toList()),
                                Stream.of(new ScriptLabelDto("label0", "value0"))
                                        .collect(Collectors.toList()), null, null)));
    }


    @Test
    void getByNameMultipleScriptsTest() {
        Script script1 = ScriptBuilder.simpleScript("script0", 0, 1, 2, 1);
        Script script2 = ScriptBuilder.simpleScript("script1", 0, 1, 2, 1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        assertEquals(1, scriptDtoService.getByName("script0").size());
        assertThat(scriptDtoService.getByName("script0"),
                CoreMatchers.hasItems(new ScriptDto("script0", "dummy script",
                        new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                        Stream.of(new ActionDto(0, "action0", "fwk.dummy",
                                "dummy action", null, null, null, false, false,
                                0,
                                Stream.of(
                                        new ActionParameterDto("parameter0", "value0"),
                                        new ActionParameterDto("parameter1", "value1"))
                                        .collect(Collectors.toList())))
                                .collect(Collectors.toList()),
                        Stream.of(new ScriptLabelDto("label0", "value0"))
                                .collect(Collectors.toList()), null, null)));
    }

    @Test
    void getByNameSingleVersionWithMultipleExecutionsExecutionExpansionEnabledTest() {
        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        String runId1 = UUID.randomUUID().toString();
        ScriptResult scriptResult1 = new ScriptResultBuilder(runId1, 0)
                .scriptName("script0")
                .scriptVersion(0L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:10"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:20"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult1);
        String runId2 = UUID.randomUUID().toString();
        ScriptResult scriptResult2 = new ScriptResultBuilder(runId2, 0)
                .scriptName("script0")
                .scriptVersion(0L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.ERROR)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:05"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:15"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult2);

        assertEquals(1, scriptDtoService.getByName("script0", Stream.of("execution").collect(Collectors.toList())).size());
        assertEquals(new ScriptDto("script0", "dummy script",
                        new ScriptVersionDto(0, "dummy version"), new ArrayList<>(),
                        Stream.of(
                                new ActionDto(0, "action0", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList())),
                                new ActionDto(1, "action1", "fwk.dummy",
                                        "dummy action", null, null, null, false, false,
                                        0,
                                        Stream.of(
                                                new ActionParameterDto("parameter0", "value0"),
                                                new ActionParameterDto("parameter1", "value1"))
                                                .collect(Collectors.toList()))
                        ).collect(Collectors.toList()),
                        Stream.of(
                                new ScriptLabelDto("label0", "value0"),
                                new ScriptLabelDto("label1", "value1")
                        ).collect(Collectors.toList()),
                        new ScriptExecutionInformation(null, Stream.of(
                                new ScriptExecutionDto(
                                        runId1,
                                        "test",
                                        ScriptRunStatus.SUCCESS,
                                        LocalDateTime.parse("2020-05-20T10:10:10"),
                                        LocalDateTime.parse("2020-05-20T10:10:20"))
                        ).collect(Collectors.toList())), null),
                scriptDtoService.getByName("script0", Stream.of("execution").collect(Collectors.toList())).get(0));
    }

    @Test
    void getByNameMultipleVersionWithMultipleExecutionsExecutionExpansionEnabledTest() {
        Script script1 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        Script script2 = ScriptBuilder.simpleScript("script0", 1, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        String runId11 = UUID.randomUUID().toString();
        ScriptResult scriptResult11 = new ScriptResultBuilder(runId11, 0)
                .scriptName("script0")
                .scriptVersion(0L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:10"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:20"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult11);
        String runId12 = UUID.randomUUID().toString();
        ScriptResult scriptResult12 = new ScriptResultBuilder(runId12, 0)
                .scriptName("script0")
                .scriptVersion(0L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.ERROR)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:05"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:15"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult12);
        String runId21 = UUID.randomUUID().toString();
        ScriptResult scriptResult21 = new ScriptResultBuilder(runId21, 0)
                .scriptName("script0")
                .scriptVersion(1L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:13"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:14"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult21);
        String runId22 = UUID.randomUUID().toString();
        ScriptResult scriptResult22 = new ScriptResultBuilder(runId22, 0)
                .scriptName("script0")
                .scriptVersion(1L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:32"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:33"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult22);
        assertEquals(2, scriptDtoService.getByName("script0", Stream.of("execution").collect(Collectors.toList())).size());
        assertThat(scriptDtoService.getByName("script0", Stream.of("execution").collect(Collectors.toList())),
                hasItems(
                        new ScriptDto("script0", "dummy script",
                                new ScriptVersionDto(0, "dummy version"),
                                new ArrayList<>(),
                                Stream.of(
                                        new ActionDto(0, "action0", "fwk.dummy",
                                                "dummy action", null, null, null, false, false,
                                                0,
                                                Stream.of(
                                                        new ActionParameterDto("parameter0", "value0"),
                                                        new ActionParameterDto("parameter1", "value1"))
                                                        .collect(Collectors.toList())),
                                        new ActionDto(1, "action1", "fwk.dummy",
                                                "dummy action", null, null, null, false, false,
                                                0,
                                                Stream.of(
                                                        new ActionParameterDto("parameter0", "value0"),
                                                        new ActionParameterDto("parameter1", "value1"))
                                                        .collect(Collectors.toList()))
                                ).collect(Collectors.toList()),
                                Stream.of(
                                        new ScriptLabelDto("label0", "value0"),
                                        new ScriptLabelDto("label1", "value1")
                                ).collect(Collectors.toList()),
                                new ScriptExecutionInformation(null, Stream.of(
                                        new ScriptExecutionDto(
                                                runId11,
                                                "test",
                                                ScriptRunStatus.SUCCESS,
                                                LocalDateTime.parse("2020-05-20T10:10:10"),
                                                LocalDateTime.parse("2020-05-20T10:10:20"))
                                ).collect(Collectors.toList())), null),
                        new ScriptDto("script0", "dummy script",
                                new ScriptVersionDto(1, "dummy version"),
                                new ArrayList<>(),
                                Stream.of(
                                        new ActionDto(0, "action0", "fwk.dummy",
                                                "dummy action", null, null, null, false, false,
                                                0,
                                                Stream.of(
                                                        new ActionParameterDto("parameter0", "value0"),
                                                        new ActionParameterDto("parameter1", "value1"))
                                                        .collect(Collectors.toList())),
                                        new ActionDto(1, "action1", "fwk.dummy",
                                                "dummy action", null, null, null, false, false,
                                                0,
                                                Stream.of(
                                                        new ActionParameterDto("parameter0", "value0"),
                                                        new ActionParameterDto("parameter1", "value1"))
                                                        .collect(Collectors.toList()))
                                ).collect(Collectors.toList()),
                                Stream.of(
                                        new ScriptLabelDto("label0", "value0"),
                                        new ScriptLabelDto("label1", "value1")
                                ).collect(Collectors.toList()),
                                new ScriptExecutionInformation(null, Stream.of(
                                        new ScriptExecutionDto(
                                                runId22,
                                                "test",
                                                ScriptRunStatus.SUCCESS,
                                                LocalDateTime.parse("2020-05-20T10:10:32"),
                                                LocalDateTime.parse("2020-05-20T10:10:33"))
                                ).collect(Collectors.toList())), null))
        );
    }


}
