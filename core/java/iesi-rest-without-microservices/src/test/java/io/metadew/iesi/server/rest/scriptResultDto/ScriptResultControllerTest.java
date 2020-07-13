package io.metadew.iesi.server.rest.scriptResultDto;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.server.rest.builder.scriptresult.ScriptResultBuilder;
import io.metadew.iesi.server.rest.error.CustomGlobalExceptionHandler;
import io.metadew.iesi.server.rest.scriptResultDto.dto.ScriptResultDto;
import io.metadew.iesi.server.rest.scriptResultDto.dto.ScriptResultDtoModelAssembler;
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
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(ScriptResultController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ScriptResultController.class, ScriptResultDtoModelAssembler.class, ScriptResultDto.class, CustomGlobalExceptionHandler.class})
class ScriptResultControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ScriptResultService scriptResultService;


    @Test
    void getAllNoResultTest() throws Exception {
        // Mock Service
        List<ScriptResult> scriptResultList = new ArrayList<>();
        given(scriptResultService.getAll()).willReturn(scriptResultList);

        mvc.perform(get("/script_results")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    void getAllScript2ResultsTest() throws Exception {
        List<ScriptResult> scriptResultList = new ArrayList<>();
        ScriptResult scriptResult1 = ScriptResultBuilder.simpleScriptResult(1);
        ScriptResult scriptResult2 = ScriptResultBuilder.simpleScriptResult(2);
        scriptResultList.add(scriptResult1);
        scriptResultList.add(scriptResult2);
        given(scriptResultService.getAll()).willReturn(scriptResultList);

        // Request
        mvc.perform(get("/script_results")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded", hasSize(2)))
                .andExpect(jsonPath("$._embedded[0].runID",
                        is(scriptResult1.getMetadataKey().getRunId())))
                .andExpect(jsonPath("$._embedded[0].processId",
                        is((int) ((long) scriptResult1.getMetadataKey().getProcessId()))))
                .andExpect(jsonPath("$._embedded[0].scriptId",
                        is(scriptResult1.getScriptId())))
                .andExpect(jsonPath("$._embedded[0].scriptName",
                        is(scriptResult1.getScriptName())))
                .andExpect(jsonPath("$._embedded[0].scriptVersion",
                        is((int) ((long) scriptResult1.getScriptVersion()))))
                .andExpect(jsonPath("$._embedded[0].environment",
                        is(scriptResult1.getEnvironment())))
                .andExpect(jsonPath("$._embedded[0].status",
                        is(String.valueOf(scriptResult1.getStatus()))))
                .andExpect(jsonPath("$._embedded[0].startTimestamp",
                        is(scriptResult1.getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0].endTimestamp",
                        is(scriptResult1.getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0]._links.self.href",
                        is("http://localhost/script_results/" + scriptResult1.getMetadataKey().getRunId() + "/" + scriptResult1.getMetadataKey().getProcessId())))
                .andExpect(jsonPath("$._embedded[1].runID",
                        is(scriptResult2.getMetadataKey().getRunId())))
                .andExpect(jsonPath("$._embedded[1].processId",
                        is((int) ((long) scriptResult2.getMetadataKey().getProcessId()))))
                .andExpect(jsonPath("$._embedded[1].scriptId",
                        is(scriptResult2.getScriptId())))
                .andExpect(jsonPath("$._embedded[1].scriptName",
                        is(scriptResult2.getScriptName())))
                .andExpect(jsonPath("$._embedded[1].scriptVersion",
                        is((int) ((long) scriptResult2.getScriptVersion()))))
                .andExpect(jsonPath("$._embedded[1].environment",
                        is(scriptResult2.getEnvironment())))
                .andExpect(jsonPath("$._embedded[1].status",
                        is(String.valueOf(scriptResult2.getStatus()))))
                .andExpect(jsonPath("$._embedded[1].startTimestamp",
                        is(scriptResult2.getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].endTimestamp",
                        is(scriptResult2.getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1]._links.self.href",
                        is("http://localhost/script_results/" + scriptResult2.getMetadataKey().getRunId() + "/" + scriptResult2.getMetadataKey().getProcessId())));
    }

    @Test
    void getByRunIdNoResultTest() throws Exception {
        // Mock Service
        List<ScriptResult> scriptResultList = new ArrayList<>();
        given(scriptResultService.getByRunId("notTheSameId")).willReturn(scriptResultList);

        // Request
        mvc.perform(get("/script_results/notTheSameId")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    void getByRunId2Results() throws Exception {
        // Mock Service
        List<ScriptResult> scriptResultList = new ArrayList<>();
        ScriptResult scriptResult1 = ScriptResultBuilder.simpleScriptResult(1, "sameId");
        ScriptResult scriptResult2 = ScriptResultBuilder.simpleScriptResult(2, "sameId");
        scriptResultList.add(scriptResult1);
        scriptResultList.add(scriptResult2);
        given(scriptResultService.getByRunId("sameId")).willReturn(scriptResultList);

        // Request
        mvc.perform(get("/script_results/sameId")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded", hasSize(2)))
                .andExpect(jsonPath("$._embedded[0].runID", is(scriptResult1.getMetadataKey().getRunId())))
                .andExpect(jsonPath("$._embedded[0].processId", is((int) ((long) scriptResult1.getMetadataKey().getProcessId()))))
                .andExpect(jsonPath("$._embedded[0].scriptId", is(scriptResult1.getScriptId())))
                .andExpect(jsonPath("$._embedded[0].scriptName", is(scriptResult1.getScriptName())))
                .andExpect(jsonPath("$._embedded[0].scriptVersion", is((int) ((long) scriptResult1.getScriptVersion()))))
                .andExpect(jsonPath("$._embedded[0].environment", is(scriptResult1.getEnvironment())))
                .andExpect(jsonPath("$._embedded[0].status", is(String.valueOf(scriptResult1.getStatus()))))
                .andExpect(jsonPath("$._embedded[0].startTimestamp", is(scriptResult1.getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0].endTimestamp", is(scriptResult1.getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[0]._links.self.href", is("http://localhost/script_results/" + scriptResult1.getMetadataKey().getRunId() + "/" + scriptResult1.getMetadataKey().getProcessId())))
                .andExpect(jsonPath("$._embedded", hasSize(2)))
                .andExpect(jsonPath("$._embedded[1].runID", is(scriptResult2.getMetadataKey().getRunId())))
                .andExpect(jsonPath("$._embedded[1].processId", is((int) ((long) scriptResult2.getMetadataKey().getProcessId()))))
                .andExpect(jsonPath("$._embedded[1].scriptId", is(scriptResult2.getScriptId())))
                .andExpect(jsonPath("$._embedded[1].scriptName", is(scriptResult2.getScriptName())))
                .andExpect(jsonPath("$._embedded[1].scriptVersion", is((int) ((long) scriptResult2.getScriptVersion()))))
                .andExpect(jsonPath("$._embedded[1].environment", is(scriptResult2.getEnvironment())))
                .andExpect(jsonPath("$._embedded[1].status", is(String.valueOf(scriptResult2.getStatus()))))
                .andExpect(jsonPath("$._embedded[1].startTimestamp", is(scriptResult2.getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1].endTimestamp", is(scriptResult2.getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._embedded[1]._links.self.href", is("http://localhost/script_results/" + scriptResult2.getMetadataKey().getRunId() + "/" + scriptResult2.getMetadataKey().getProcessId())));
    }

    @Test
    void getByRunIdAndProcessIdSuccessfulTest() throws Exception {
        // Mock Service
        Optional<ScriptResult> optionalScriptResult = Optional.of(ScriptResultBuilder.simpleScriptResult(1));
        given(scriptResultService.getByRunIdAndProcessId("sameId", 1L)).willReturn(optionalScriptResult);
        ScriptResult scriptResult = optionalScriptResult.get();
        // Request
        mvc.perform(get("/script_results/sameId/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.runID", is(scriptResult.getMetadataKey().getRunId())))
                .andExpect(jsonPath("$.processId", is((int) ((long) scriptResult.getMetadataKey().getProcessId()))))
                .andExpect(jsonPath("$.scriptId", is(scriptResult.getScriptId())))
                .andExpect(jsonPath("$.scriptName", is(scriptResult.getScriptName())))
                .andExpect(jsonPath("$.scriptVersion", is((int) ((long) scriptResult.getScriptVersion()))))
                .andExpect(jsonPath("$.environment", is(scriptResult.getEnvironment())))
                .andExpect(jsonPath("$.status", is(String.valueOf(scriptResult.getStatus()))))
                .andExpect(jsonPath("$.startTimestamp", is(scriptResult.getStartTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$.endTimestamp", is(scriptResult.getEndTimestamp().format(SQLTools.defaultDateTimeFormatter))))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/script_results/" + scriptResult.getMetadataKey().getRunId() + "/" + scriptResult.getMetadataKey().getProcessId())));
    }

    @Test
    void getByRunIdAndProcessIdNoResultTest() throws Exception {
        // Mock Service
        Optional<ScriptResult> optionalScriptResult = Optional.empty();
        given(scriptResultService.getByRunIdAndProcessId("Id", 1L)).willReturn(optionalScriptResult);
        mvc.perform(get("/script_results/Id/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}