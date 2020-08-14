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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


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

    @Autowired
    private ScriptDtoModelAssembler scriptDtoModelAssembler;

    @BeforeEach
    void setup() {
        metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::cleanAllTables);
        //metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::dropAllTables);
        //metadataRepositoryConfiguration.getMetadataRepositories().forEach(MetadataRepository::createAllTables);
    }

    @Test
    void getAllReturnFormatTest() {
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getAll(pageable, null, false))
                .isInstanceOf(PageImpl.class);
    }

    @Test
    void getAllNoScriptsTest() {
        Pageable pageable = Pageable.unpaged();
        assertThat(
                scriptDtoService.getAll(pageable, null, false)
                        .getContent()
                        .size())
                .isEqualTo(0);
    }

    @Test
    void getAllSimpleTest() {
        ScriptResult scriptResult = new ScriptResult(new ScriptResultKey("123", 1L), 1L, " ", "", 1L, "", ScriptRunStatus.SUCCESS, LocalDateTime.now(), LocalDateTime.now());

        scriptResultConfiguration.insert(scriptResult);

        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        List<String> expansions = new ArrayList<>();
        expansions.add("");
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getAll(pageable, expansions, false).getContent().size())
                .isEqualTo(1);
        assertThat(scriptDtoService.getAll(pageable, expansions, false).getContent().get(0))
                .isEqualTo(new ScriptDto("script0", "dummy script",
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
                        ).collect(Collectors.toList()), null, null));
    }

    @Test
    void getAllMultipleScriptsTest() {
        Script script1 = ScriptBuilder.simpleScript("script0", 0, 1, 2, 1);
        Script script2 = ScriptBuilder.simpleScript("script1", 0, 1, 2, 1);
        List<String> expansions = new ArrayList<>();
        expansions.add("");
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getAll(pageable, expansions, false).getContent().size())
                .isEqualTo(2);
        assertThat(scriptDtoService.getAll(pageable, expansions, false).getContent())
                .contains(new ScriptDto("script0", "dummy script",
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
                                .collect(Collectors.toList()), null, null), new ScriptDto("script1", "dummy script",
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
                                .collect(Collectors.toList()), null, null));
    }

    @Test
    void getAllMultipleScriptVersionsTest() {
        Script script1 = ScriptBuilder.simpleScript("script0", 0, 1, 2, 1);
        Script script2 = ScriptBuilder.simpleScript("script0", 1, 1, 2, 1);
        List<String> expansions = new ArrayList<>();
        expansions.add("");
        Pageable pageable = Pageable.unpaged();
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        assertThat(scriptDtoService.getAll(pageable, expansions, false).getContent().size())
                .isEqualTo(2);
        assertThat(scriptDtoService.getAll(pageable, expansions, false).getContent())
                .contains(new ScriptDto("script0", "dummy script",
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
                                .collect(Collectors.toList()), null, null), new ScriptDto("script0", "dummy script",
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
                                .collect(Collectors.toList()), null, null));
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

        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getAll(pageable, Stream.of("execution").collect(Collectors.toList()), false).getContent().size())
                .isEqualTo(1);
        assertThat(scriptDtoService.getAll(pageable, Stream.of("execution").collect(Collectors.toList()), false).getContent().get(0))
                .isEqualTo(new ScriptDto("script0", "dummy script",
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
                        ).collect(Collectors.toList())), null));
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

        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getAll(pageable, Stream.of("execution").collect(Collectors.toList()), false).getContent().size())
                .isEqualTo(1);
        assertThat(scriptDtoService.getAll(pageable, Stream.of("execution").collect(Collectors.toList()), false).getContent().get(0))
                .isEqualTo(new ScriptDto("script0", "dummy script",
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
                        ).collect(Collectors.toList())), null));
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
        List<String> expansions = new ArrayList<>();
        expansions.add("");
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getAll(pageable, expansions, false).getContent().size())
                .isEqualTo(1);
        assertThat(scriptDtoService.getAll(pageable, expansions, false).getContent().get(0))
                .isEqualTo(new ScriptDto("script0", "dummy script",
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
                        null, null));
    }

    @Test
    void getAllLastVersionExecutionExpansionDisabledTest() {
        Script script1V1 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 0);
        Script script1V2 = ScriptBuilder.simpleScript("script0", 1, 2, 2, 0);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1V1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1V2);
        Script script2V1 = ScriptBuilder.simpleScript("script1", 0, 2, 2, 0);
        Script script2V2 = ScriptBuilder.simpleScript("script1", 1, 2, 2, 0);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2V1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2V2);

        Pageable pageable = Pageable.unpaged();

        assertThat(scriptDtoService.getAll(pageable, new ArrayList<>(), true))
                .as("The retrieved list of ScriptDto should contain these 2 scriptDto")
                .containsExactlyInAnyOrder(scriptDtoModelAssembler.convertToDto(script1V2), scriptDtoModelAssembler.convertToDto(script2V2));

    }

    @Test
    void getAllLastVersionSingleExecutionExecutionExpansionEnabledTest() {
        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        Script script22 = ScriptBuilder.simpleScript("script0", 1, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script22);
        String runId = UUID.randomUUID().toString();
        ScriptResult scriptResult = new ScriptResultBuilder(runId, 0)
                .scriptName("script0")
                .scriptVersion(1L)
                .parentProcessId(0L)
                .environment("test")
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.parse("2020-05-20T10:10:10"))
                .endTimestamp(LocalDateTime.parse("2020-05-20T10:10:20"))
                .build();
        ScriptResultConfiguration.getInstance().insert(scriptResult);
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getAll(pageable, Stream.of("execution").collect(Collectors.toList()), true).getContent().size())
                .as("There should be only one ScriptDto")
                .isEqualTo(1);

        assertThat(scriptDtoService.getAll(pageable, Stream.of("execution").collect(Collectors.toList()), true).getContent().get(0))
                .as("The retrieved ScriptDto should be equal to this ScriptDto")
                .isEqualTo(new ScriptDto("script0", "dummy script",
                        new ScriptVersionDto(1, "dummy version"), new ArrayList<>(),
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
                        ).collect(Collectors.toList())), null));
    }

    @Test
    void getAllLatestVersionNoExpansionSpecifiedTest() {
        Script script1V1 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 0);
        Script script1V2 = ScriptBuilder.simpleScript("script0", 1, 2, 2, 0);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1V1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1V2);
        Script script2V1 = ScriptBuilder.simpleScript("script1", 0, 2, 2, 0);
        Script script2V2 = ScriptBuilder.simpleScript("script1", 1, 2, 2, 0);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2V1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2V2);

        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getAll(pageable, new ArrayList<>(), true))
                .containsExactlyInAnyOrder(scriptDtoModelAssembler.toModel(script1V2), scriptDtoModelAssembler.toModel(script2V2));

    }

    @Test
    void getAllPaginationTest() {
        Script scriptA = ScriptBuilder.simpleScript("ScriptA", 0, 2, 2, 2);
        Script scriptB = ScriptBuilder.simpleScript("ScriptB", 0, 2, 2, 2);
        Script scriptC = ScriptBuilder.simpleScript("ScriptC", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptA);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptB);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptC);

        Pageable requestPage1 = PageRequest.of(0, 1);
        Page<ScriptDto> resultPage1 = scriptDtoService.getAll(requestPage1, new ArrayList<>(), false);

        assertThat(resultPage1.getContent().size())
                .isEqualTo(1);

        assertThat(resultPage1.getTotalElements())
                .isEqualTo(3);

        assertThat(resultPage1.getTotalPages())
                .isEqualTo(3);

        Pageable requestPage2 = PageRequest.of(1, 1);
        Page<ScriptDto> resultPage2 = scriptDtoService.getAll(requestPage2, new ArrayList<>(), false);

        assertThat(resultPage2.getContent().size())
                .isEqualTo(1);

        assertThat(resultPage2.getTotalElements())
                .isEqualTo(3);

        assertThat(resultPage2.getTotalPages())
                .isEqualTo(3);

        Pageable requestPage3 = PageRequest.of(2, 1);
        Page<ScriptDto> resultPage3 = scriptDtoService.getAll(requestPage3, new ArrayList<>(), false);

        assertThat(resultPage3.getContent().size())
                .isEqualTo(1);

        assertThat(resultPage3.getTotalElements())
                .isEqualTo(3);

        assertThat(resultPage3.getTotalPages())
                .isEqualTo(3);

    }

    @Test
    void getAllPaginatedSizeEqualsTotalAndOrderByNameDefaultAscTest() {
        Script scriptA = ScriptBuilder.simpleScript("ScriptA", 0, 2, 2, 2);
        Script scriptB = ScriptBuilder.simpleScript("ScriptB", 0, 2, 2, 2);
        Script scriptC = ScriptBuilder.simpleScript("ScriptC", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptA);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptB);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptC);

        int size = 3;
        int numberOfScript = 3;
        Sort sortAsc = Sort.by(Sort.DEFAULT_DIRECTION, "Name");
        Pageable requestPage1 = PageRequest.of(0, size, sortAsc);
        Page<ScriptDto> resultPage1 = scriptDtoService.getAll(requestPage1, new ArrayList<>(), false);

        assertThat(resultPage1.getContent().size())
                .describedAs("The page should only contain " + size + " elements")
                .isEqualTo(size);

        assertThat(resultPage1.getTotalElements())
                .isEqualTo(numberOfScript);

        assertThat(resultPage1.getTotalPages())
                .isEqualTo(size / numberOfScript);

        assertThat(resultPage1.getContent().get(0).getName())
                .isEqualTo(scriptA.getName());

        assertThat(resultPage1.getContent().get(1).getName())
                .isEqualTo(scriptB.getName());

        assertThat(resultPage1.getContent().get(2).getName())
                .isEqualTo(scriptC.getName());

    }

    @Test
    void getAllPaginatedAndOrderByNameDefaultAscTest() {
        Script scriptA = ScriptBuilder.simpleScript("ScriptA", 0, 2, 2, 2);
        Script scriptB = ScriptBuilder.simpleScript("ScriptB", 0, 2, 2, 2);
        Script scriptC = ScriptBuilder.simpleScript("ScriptC", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptA);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptB);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptC);

        int size = 1;
        Sort sortAsc = Sort.by(Sort.DEFAULT_DIRECTION, "Name");
        Pageable requestPage1 = PageRequest.of(0, size, sortAsc);
        Page<ScriptDto> resultPage1 = scriptDtoService.getAll(requestPage1, new ArrayList<>(), false);

        assertThat(resultPage1.getContent().size())
                .describedAs("The page should only contain " + size + " elements")
                .isEqualTo(size);

        assertThat(resultPage1.getTotalElements())
                .isEqualTo(3);

        assertThat(resultPage1.getTotalPages())
                .isEqualTo(3);

        assertThat(resultPage1.getContent().get(0).getName())
                .isEqualTo(scriptA.getName());

        Pageable requestPage2 = PageRequest.of(1, 1, sortAsc);
        Page<ScriptDto> resultPage2 = scriptDtoService.getAll(requestPage2, new ArrayList<>(), false);

        assertThat(resultPage2.getContent().size())
                .describedAs("The page should only contain " + size + " elements")
                .isEqualTo(size);

        assertThat(resultPage2.getTotalElements())
                .isEqualTo(3);

        assertThat(resultPage2.getTotalPages())
                .isEqualTo(3);

        assertThat(resultPage2.getContent().get(0).getName())
                .isEqualTo("ScriptB");

        Pageable requestPage3 = PageRequest.of(2, 1, sortAsc);
        Page<ScriptDto> resultPage3 = scriptDtoService.getAll(requestPage3, new ArrayList<>(), false);

        assertThat(resultPage3.getContent().size())
                .describedAs("The page should only contain " + size + " elements")
                .isEqualTo(size);

        assertThat(resultPage3.getTotalElements())
                .isEqualTo(3);

        assertThat(resultPage3.getTotalPages())
                .isEqualTo(3);

        assertThat(resultPage3.getContent().get(0).getName())
                .isEqualTo("ScriptC");

    }

    @Test
    void getAllPaginatedAndOrderByNameDscTest() {
        Script scriptA = ScriptBuilder.simpleScript("ScriptA", 0, 2, 2, 2);
        Script scriptB = ScriptBuilder.simpleScript("ScriptB", 0, 2, 2, 2);
        Script scriptC = ScriptBuilder.simpleScript("ScriptC", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptA);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptB);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(scriptC);

        Sort sortDesc = Sort.by(Sort.Direction.DESC, "Name");
        Pageable requestPage1 = PageRequest.of(0, 1, sortDesc);
        Page<ScriptDto> resultPage1 = scriptDtoService.getAll(requestPage1, new ArrayList<>(), false);

        assertThat(resultPage1.getContent().size())
                .isEqualTo(1);

        assertThat(resultPage1.getTotalElements())
                .isEqualTo(3);

        assertThat(resultPage1.getTotalPages())
                .isEqualTo(3);

        assertThat(resultPage1.getContent().get(0).getName())
                .isEqualTo("ScriptC");

        Pageable requestPage2 = PageRequest.of(1, 1, sortDesc);
        Page<ScriptDto> resultPage2 = scriptDtoService.getAll(requestPage2, new ArrayList<>(), false);

        assertThat(resultPage2.getContent().size())
                .isEqualTo(1);

        assertThat(resultPage2.getTotalElements())
                .isEqualTo(3);

        assertThat(resultPage2.getTotalPages())
                .isEqualTo(3);

        assertThat(resultPage2.getContent().get(0).getName())
                .isEqualTo("ScriptB");

        Pageable requestPage3 = PageRequest.of(2, 1, sortDesc);
        Page<ScriptDto> resultPage3 = scriptDtoService.getAll(requestPage3, new ArrayList<>(), false);

        assertThat(resultPage3.getContent().size())
                .isEqualTo(1);

        assertThat(resultPage3.getTotalElements())
                .isEqualTo(3);

        assertThat(resultPage3.getTotalPages())
                .isEqualTo(3);

        assertThat(resultPage3.getContent().get(0).getName())
                .isEqualTo("ScriptA");
    }

    @Test
    void getByNameReturnFormatTest() {
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getByName(pageable, null, new ArrayList<>(), false))
                .isInstanceOf(PageImpl.class);
    }

    @Test
    void getByNameSimpleTest() {
        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getByName(pageable, "script0", new ArrayList<>(), false).getContent().size())
                .isEqualTo(1);
        assertThat(scriptDtoService.getByName(pageable, "script0", new ArrayList<>(), false).getContent().get(0))
                .isEqualTo(new ScriptDto("script0", "dummy script",
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
                        ).collect(Collectors.toList()), null, null));
    }

    @Test
    void getByNameSimpleNoRetrievedDataTest() {
        Script script12 = ScriptBuilder.simpleScript("script0", 0, 2, 2, 2);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script12);
        Pageable pageable = Pageable.unpaged();
        assertThat(scriptDtoService.getByName(pageable, "script1", new ArrayList<>(), false).getContent().size())
                .isEqualTo(0);
    }

    @Test
    void getByNameMultipleScriptVersionsTest() {
        Script script1 = ScriptBuilder.simpleScript("script0", 0, 1, 2, 1);
        Script script2 = ScriptBuilder.simpleScript("script0", 1, 1, 2, 1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        assertThat(scriptDtoService.getByName(Pageable.unpaged(), "script0", new ArrayList<>(), false).getContent().size())
                .isEqualTo(2);
        assertThat(scriptDtoService.getByName(Pageable.unpaged(), "script0", new ArrayList<>(), false).getContent())
                .contains(new ScriptDto("script0", "dummy script",
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
                                .collect(Collectors.toList()), null, null), new ScriptDto("script0", "dummy script",
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
                                .collect(Collectors.toList()), null, null));
    }

    @Test
    void getByNameMultipleScriptsTest() {
        Script script1 = ScriptBuilder.simpleScript("script0", 0, 1, 2, 1);
        Script script2 = ScriptBuilder.simpleScript("script1", 0, 1, 2, 1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script1);
        metadataRepositoryConfiguration.getDesignMetadataRepository().save(script2);
        assertThat(scriptDtoService.getByName(Pageable.unpaged(), "script0", new ArrayList<>(), false).getContent().size())
                .isEqualTo(1);
        assertThat(scriptDtoService.getByName(Pageable.unpaged(), "script0", new ArrayList<>(), false).getContent())
                .contains(new ScriptDto("script0", "dummy script",
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
                                .collect(Collectors.toList()), null, null));
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

        assertThat(scriptDtoService.getByName(Pageable.unpaged(), "script0", Stream.of("execution").collect(Collectors.toList()), false).getContent().size())
                .isEqualTo(1);
        assertThat(scriptDtoService.getByName(Pageable.unpaged(), "script0", Stream.of("execution").collect(Collectors.toList()), false).getContent().get(0))
                .isEqualTo(new ScriptDto("script0", "dummy script",
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
                        ).collect(Collectors.toList())), null));
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
        assertThat(scriptDtoService.getByName(Pageable.unpaged(), "script0", Stream.of("execution").collect(Collectors.toList()), false).getContent().size())
                .isEqualTo(2);
        assertThat(scriptDtoService.getByName(Pageable.unpaged(), "script0", Stream.of("execution").collect(Collectors.toList()), false).getContent())
                .contains(new ScriptDto("script0", "dummy script",
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
                        ).collect(Collectors.toList())), null), new ScriptDto("script0", "dummy script",
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
                        ).collect(Collectors.toList())), null));
    }

    @Test
    void getByNameAndVersionEmptyDB() {
        assertThat(scriptDtoService.getByNameAndVersion("", 0L, new ArrayList<>()))
                .describedAs("The returned value should be an optional")
                .isInstanceOf(Optional.class);

        assertThat(scriptDtoService.getByNameAndVersion("", 0L, new ArrayList<>()).isPresent())
                .describedAs("The returned optional should be empty")
                .isFalse();
    }
}
