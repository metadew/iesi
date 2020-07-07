package io.metadew.iesi.server.rest.script;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.server.rest.builder.script.ScriptDtoBuilder;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import io.metadew.iesi.server.rest.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.script.dto.ScriptDtoModelAssembler;
import io.metadew.iesi.server.rest.script.dto.ScriptDtoService;
import io.metadew.iesi.server.rest.script.dto.ScriptPostDtoService;
import io.metadew.iesi.server.rest.script.dto.action.ScriptActionDtoService;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDtoService;
import io.metadew.iesi.server.rest.script.dto.parameter.ScriptParameterDtoService;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDtoService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ScriptController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ScriptController.class, CustomGlobalExceptionHandler.class, ScriptDtoModelAssembler.class,
        ScriptPostDtoService.class, ScriptParameterDtoService.class, ScriptLabelDtoService.class, ScriptActionDtoService.class,
        ScriptVersionDtoService.class})
class ScriptControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ScriptDtoService scriptDtoService;

    @Test
    void getAllNoResult() throws Exception {
        // Mock Service
        List<ScriptDto> scriptDtoList = new ArrayList<>();
        given(scriptDtoService.getAll(null, false)).willReturn(scriptDtoList);

        mvc.perform(get("/scripts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    void getAll2ResultsAndSerializationTest() throws Exception {
        // Mock Service
        ScriptDto scriptDto0 = ScriptDtoBuilder.simpleScriptDto("1stScript", 0);
        ScriptDto scriptDto1 = ScriptDtoBuilder.simpleScriptDto("1stScript", 1);
        List<ScriptDto> scriptDtoList = Stream.of(scriptDto0, scriptDto1).collect(Collectors.toList());
        given(scriptDtoService.getAll(null, false)).willReturn(scriptDtoList);

        mvc.perform(get("/scripts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded[0].name", is(scriptDto0.getName())))
                .andExpect(jsonPath("$._embedded[0].description", is(scriptDto0.getDescription())))
                .andExpect(jsonPath("$._embedded[0].version.number", is((int) scriptDto0.getVersion().getNumber())))
                .andExpect(jsonPath("$._embedded[0].version.description", is(scriptDto0.getVersion().getDescription())))
                .andExpect(jsonPath("$._embedded[0].parameters").isArray())
                .andExpect(jsonPath("$._embedded[0].parameters[0].name", is(scriptDto0.getParameters().get(0).getName())))
                .andExpect(jsonPath("$._embedded[0].parameters[0].value", is(scriptDto0.getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[0].parameters[1].name", is(scriptDto0.getParameters().get(1).getName())))
                .andExpect(jsonPath("$._embedded[0].parameters[1].value", is(scriptDto0.getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[0].actions").isArray())
                .andExpect(jsonPath("$._embedded[0].actions[0].number", is((int) scriptDto0.getActions().get(0).getNumber())))
                .andExpect(jsonPath("$._embedded[0].actions[0].name", is(scriptDto0.getActions().get(0).getName())))
                .andExpect(jsonPath("$._embedded[0].actions[0].type", is(scriptDto0.getActions().get(0).getType())))
                .andExpect(jsonPath("$._embedded[0].actions[0].description", is(scriptDto0.getActions().get(0).getDescription())))
                .andExpect(jsonPath("$._embedded[0].actions[0].component", is(scriptDto0.getActions().get(0).getComponent())))
                .andExpect(jsonPath("$._embedded[0].actions[0].condition", is(scriptDto0.getActions().get(0).getCondition())))
                .andExpect(jsonPath("$._embedded[0].actions[0].iteration", is(scriptDto0.getActions().get(0).getIteration())))
                .andExpect(jsonPath("$._embedded[0].actions[0].errorExpected", is(scriptDto0.getActions().get(0).isErrorExpected())))
                .andExpect(jsonPath("$._embedded[0].actions[0].errorStop", is(scriptDto0.getActions().get(0).isErrorStop())))
                .andExpect(jsonPath("$._embedded[0].actions[0].retries", is(scriptDto0.getActions().get(0).getRetries())))
                .andExpect(jsonPath("$._embedded[0].actions[0].parameters[0].name", is(scriptDto0.getActions().get(0).getParameters().get(0).getName())))
                .andExpect(jsonPath("$._embedded[0].actions[0].parameters[0].value", is(scriptDto0.getActions().get(0).getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[0].actions[0].parameters[1].name", is(scriptDto0.getActions().get(0).getParameters().get(1).getName())))
                .andExpect(jsonPath("$._embedded[0].actions[0].parameters[1].value", is(scriptDto0.getActions().get(0).getParameters().get(1).getValue())))
                .andExpect(jsonPath("$._embedded[0].labels[0].name", is(scriptDto0.getLabels().get(0).getName())))
                .andExpect(jsonPath("$._embedded[0].labels[0].value", is(scriptDto0.getLabels().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[0].labels[1].name", is(scriptDto0.getLabels().get(1).getName())))
                .andExpect(jsonPath("$._embedded[0].labels[1].value", is(scriptDto0.getLabels().get(1).getValue())))
                .andExpect(jsonPath("$._embedded[0].execution.total", is((int) (long) scriptDto0.getScriptExecutionInformation().getTotal())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].runId", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getRunId())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].environment", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getEnvironment())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].runStatus", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getRunStatus().toString())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].startTimestamp", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].endTimestamp", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].runId", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getRunId())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].environment", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getEnvironment())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].runStatus", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getRunStatus().toString())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].startTimestamp", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].endTimestamp", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].name", is(scriptDto1.getName())))
                .andExpect(jsonPath("$._embedded[1].description", is(scriptDto1.getDescription())))
                .andExpect(jsonPath("$._embedded[1].version.number", is((int) scriptDto1.getVersion().getNumber())))
                .andExpect(jsonPath("$._embedded[1].version.description", is(scriptDto1.getVersion().getDescription())))
                .andExpect(jsonPath("$._embedded[1].parameters").isArray())
                .andExpect(jsonPath("$._embedded[1].parameters[0].name", is(scriptDto1.getParameters().get(0).getName())))
                .andExpect(jsonPath("$._embedded[1].parameters[0].value", is(scriptDto1.getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[1].parameters[1].name", is(scriptDto1.getParameters().get(1).getName())))
                .andExpect(jsonPath("$._embedded[1].parameters[1].value", is(scriptDto1.getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[1].actions").isArray())
                .andExpect(jsonPath("$._embedded[1].actions[0].number", is((int) scriptDto1.getActions().get(0).getNumber())))
                .andExpect(jsonPath("$._embedded[1].actions[0].name", is(scriptDto1.getActions().get(0).getName())))
                .andExpect(jsonPath("$._embedded[1].actions[0].type", is(scriptDto1.getActions().get(0).getType())))
                .andExpect(jsonPath("$._embedded[1].actions[0].description", is(scriptDto1.getActions().get(0).getDescription())))
                .andExpect(jsonPath("$._embedded[1].actions[0].component", is(scriptDto1.getActions().get(0).getComponent())))
                .andExpect(jsonPath("$._embedded[1].actions[0].condition", is(scriptDto1.getActions().get(0).getCondition())))
                .andExpect(jsonPath("$._embedded[1].actions[0].iteration", is(scriptDto1.getActions().get(0).getIteration())))
                .andExpect(jsonPath("$._embedded[1].actions[0].errorExpected", is(scriptDto1.getActions().get(0).isErrorExpected())))
                .andExpect(jsonPath("$._embedded[1].actions[0].errorStop", is(scriptDto1.getActions().get(0).isErrorStop())))
                .andExpect(jsonPath("$._embedded[1].actions[0].retries", is(scriptDto1.getActions().get(0).getRetries())))
                .andExpect(jsonPath("$._embedded[1].actions[0].parameters[0].name", is(scriptDto1.getActions().get(0).getParameters().get(0).getName())))
                .andExpect(jsonPath("$._embedded[1].actions[0].parameters[0].value", is(scriptDto1.getActions().get(0).getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[1].actions[0].parameters[1].name", is(scriptDto1.getActions().get(0).getParameters().get(1).getName())))
                .andExpect(jsonPath("$._embedded[1].actions[0].parameters[1].value", is(scriptDto1.getActions().get(0).getParameters().get(1).getValue())))
                .andExpect(jsonPath("$._embedded[1].labels[0].name", is(scriptDto1.getLabels().get(0).getName())))
                .andExpect(jsonPath("$._embedded[1].labels[0].value", is(scriptDto1.getLabels().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[1].labels[1].name", is(scriptDto1.getLabels().get(1).getName())))
                .andExpect(jsonPath("$._embedded[1].labels[1].value", is(scriptDto1.getLabels().get(1).getValue())))
                .andExpect(jsonPath("$._embedded[1].execution.total", is((int) (long) scriptDto1.getScriptExecutionInformation().getTotal())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].runId", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getRunId())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].environment", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getEnvironment())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].runStatus", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getRunStatus().toString())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].startTimestamp", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].endTimestamp", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].runId", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getRunId())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].environment", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getEnvironment())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].runStatus", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getRunStatus().toString())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].startTimestamp", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].endTimestamp", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))));
    }

    @Test
    void getAllLastVersion() throws Exception {
        // Mock Service
        ScriptDto scriptDto0 = ScriptDtoBuilder.simpleScriptDto("1Script", 2);
        ScriptDto scriptDto1 = ScriptDtoBuilder.simpleScriptDto("AnotherScript", 3);
        List<ScriptDto> scriptDtoList = Stream.of(scriptDto0, scriptDto1).collect(Collectors.toList());
        given(scriptDtoService.getAll(null, true)).willReturn(scriptDtoList);

        mvc.perform(get("/scripts?version=latest").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Check Json format and data
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$._embedded[0].name", is(scriptDto0.getName())))
                .andExpect(jsonPath("$._embedded[0].description", is(scriptDto0.getDescription())))
                .andExpect(jsonPath("$._embedded[0].version.number", is((int) scriptDto0.getVersion().getNumber())))
                .andExpect(jsonPath("$._embedded[0].version.description", is(scriptDto0.getVersion().getDescription())))
                .andExpect(jsonPath("$._embedded[0].parameters").isArray())
                .andExpect(jsonPath("$._embedded[0].parameters[0].name", is(scriptDto0.getParameters().get(0).getName())))
                .andExpect(jsonPath("$._embedded[0].parameters[0].value", is(scriptDto0.getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[0].parameters[1].name", is(scriptDto0.getParameters().get(1).getName())))
                .andExpect(jsonPath("$._embedded[0].parameters[1].value", is(scriptDto0.getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[0].actions").isArray())
                .andExpect(jsonPath("$._embedded[0].actions[0].number", is((int) scriptDto0.getActions().get(0).getNumber())))
                .andExpect(jsonPath("$._embedded[0].actions[0].name", is(scriptDto0.getActions().get(0).getName())))
                .andExpect(jsonPath("$._embedded[0].actions[0].type", is(scriptDto0.getActions().get(0).getType())))
                .andExpect(jsonPath("$._embedded[0].actions[0].description", is(scriptDto0.getActions().get(0).getDescription())))
                .andExpect(jsonPath("$._embedded[0].actions[0].component", is(scriptDto0.getActions().get(0).getComponent())))
                .andExpect(jsonPath("$._embedded[0].actions[0].condition", is(scriptDto0.getActions().get(0).getCondition())))
                .andExpect(jsonPath("$._embedded[0].actions[0].iteration", is(scriptDto0.getActions().get(0).getIteration())))
                .andExpect(jsonPath("$._embedded[0].actions[0].errorExpected", is(scriptDto0.getActions().get(0).isErrorExpected())))
                .andExpect(jsonPath("$._embedded[0].actions[0].errorStop", is(scriptDto0.getActions().get(0).isErrorStop())))
                .andExpect(jsonPath("$._embedded[0].actions[0].retries", is(scriptDto0.getActions().get(0).getRetries())))
                .andExpect(jsonPath("$._embedded[0].actions[0].parameters[0].name", is(scriptDto0.getActions().get(0).getParameters().get(0).getName())))
                .andExpect(jsonPath("$._embedded[0].actions[0].parameters[0].value", is(scriptDto0.getActions().get(0).getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[0].actions[0].parameters[1].name", is(scriptDto0.getActions().get(0).getParameters().get(1).getName())))
                .andExpect(jsonPath("$._embedded[0].actions[0].parameters[1].value", is(scriptDto0.getActions().get(0).getParameters().get(1).getValue())))
                .andExpect(jsonPath("$._embedded[0].labels[0].name", is(scriptDto0.getLabels().get(0).getName())))
                .andExpect(jsonPath("$._embedded[0].labels[0].value", is(scriptDto0.getLabels().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[0].labels[1].name", is(scriptDto0.getLabels().get(1).getName())))
                .andExpect(jsonPath("$._embedded[0].labels[1].value", is(scriptDto0.getLabels().get(1).getValue())))
                .andExpect(jsonPath("$._embedded[0].execution.total", is((int) (long) scriptDto0.getScriptExecutionInformation().getTotal())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].runId", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getRunId())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].environment", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getEnvironment())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].runStatus", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getRunStatus().toString())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].startTimestamp", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[0].endTimestamp", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].runId", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getRunId())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].environment", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getEnvironment())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].runStatus", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getRunStatus().toString())))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].startTimestamp", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0].execution.mostRecent[1].endTimestamp", is(scriptDto0.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].name", is(scriptDto1.getName())))
                .andExpect(jsonPath("$._embedded[1].description", is(scriptDto1.getDescription())))
                .andExpect(jsonPath("$._embedded[1].version.number", is((int) scriptDto1.getVersion().getNumber())))
                .andExpect(jsonPath("$._embedded[1].version.description", is(scriptDto1.getVersion().getDescription())))
                .andExpect(jsonPath("$._embedded[1].parameters").isArray())
                .andExpect(jsonPath("$._embedded[1].parameters[0].name", is(scriptDto1.getParameters().get(0).getName())))
                .andExpect(jsonPath("$._embedded[1].parameters[0].value", is(scriptDto1.getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[1].parameters[1].name", is(scriptDto1.getParameters().get(1).getName())))
                .andExpect(jsonPath("$._embedded[1].parameters[1].value", is(scriptDto1.getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[1].actions").isArray())
                .andExpect(jsonPath("$._embedded[1].actions[0].number", is((int) scriptDto1.getActions().get(0).getNumber())))
                .andExpect(jsonPath("$._embedded[1].actions[0].name", is(scriptDto1.getActions().get(0).getName())))
                .andExpect(jsonPath("$._embedded[1].actions[0].type", is(scriptDto1.getActions().get(0).getType())))
                .andExpect(jsonPath("$._embedded[1].actions[0].description", is(scriptDto1.getActions().get(0).getDescription())))
                .andExpect(jsonPath("$._embedded[1].actions[0].component", is(scriptDto1.getActions().get(0).getComponent())))
                .andExpect(jsonPath("$._embedded[1].actions[0].condition", is(scriptDto1.getActions().get(0).getCondition())))
                .andExpect(jsonPath("$._embedded[1].actions[0].iteration", is(scriptDto1.getActions().get(0).getIteration())))
                .andExpect(jsonPath("$._embedded[1].actions[0].errorExpected", is(scriptDto1.getActions().get(0).isErrorExpected())))
                .andExpect(jsonPath("$._embedded[1].actions[0].errorStop", is(scriptDto1.getActions().get(0).isErrorStop())))
                .andExpect(jsonPath("$._embedded[1].actions[0].retries", is(scriptDto1.getActions().get(0).getRetries())))
                .andExpect(jsonPath("$._embedded[1].actions[0].parameters[0].name", is(scriptDto1.getActions().get(0).getParameters().get(0).getName())))
                .andExpect(jsonPath("$._embedded[1].actions[0].parameters[0].value", is(scriptDto1.getActions().get(0).getParameters().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[1].actions[0].parameters[1].name", is(scriptDto1.getActions().get(0).getParameters().get(1).getName())))
                .andExpect(jsonPath("$._embedded[1].actions[0].parameters[1].value", is(scriptDto1.getActions().get(0).getParameters().get(1).getValue())))
                .andExpect(jsonPath("$._embedded[1].labels[0].name", is(scriptDto1.getLabels().get(0).getName())))
                .andExpect(jsonPath("$._embedded[1].labels[0].value", is(scriptDto1.getLabels().get(0).getValue())))
                .andExpect(jsonPath("$._embedded[1].labels[1].name", is(scriptDto1.getLabels().get(1).getName())))
                .andExpect(jsonPath("$._embedded[1].labels[1].value", is(scriptDto1.getLabels().get(1).getValue())))
                .andExpect(jsonPath("$._embedded[1].execution.total", is((int) (long) scriptDto1.getScriptExecutionInformation().getTotal())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].runId", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getRunId())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].environment", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getEnvironment())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].runStatus", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getRunStatus().toString())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].startTimestamp", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[0].endTimestamp", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(0).getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].runId", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getRunId())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].environment", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getEnvironment())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].runStatus", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getRunStatus().toString())))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].startTimestamp", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].execution.mostRecent[1].endTimestamp", is(scriptDto1.getScriptExecutionInformation().getScriptExecutionDtos().get(1).getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))));
    }

    @Test
    void getByName() {
        // Todo: Write Test

    }

    @Test
    void getByNameAndVersion() {
        // Todo: Write Test
    }

    @Test
    void post() {
        // Todo: Write Test
    }

    @Test
    void putAll() {
        // Todo: Write Test
    }

    @Test
    void put() {
        // Todo: Write Test
    }

    @Test
    void deleteByName() {
        // Todo: Write Test
    }

    @Test
    void delete() {
        // Todo: Write Test
    }

}