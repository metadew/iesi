package io.metadew.iesi.server.rest.script.result;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.configuration.exception.MetadataDoesNotExistException;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.server.rest.script.result.dto.ScriptResultDto;
import io.metadew.iesi.server.rest.script.result.dto.ScriptResultDtoModelAssembler;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(ScriptResultController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ScriptResultController.class, ScriptResultDtoModelAssembler.class, ScriptResultDto.class})
class ScriptResultControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ScriptResultService scriptResultService;

    @Test
    void getAllStatus200() throws Exception {
        // Mock Service
        List<ScriptResult> scriptResultList = new ArrayList<>();
        scriptResultList.add(createADummyScriptResult(1));
        given(scriptResultService.getAll()).willReturn(scriptResultList);

        mvc.perform(get("/scriptResult").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllNoResult() throws Exception {
        // Mock Service
        List<ScriptResult> scriptResultList = new ArrayList<>();
        given(scriptResultService.getAll()).willReturn(scriptResultList);

        mvc.perform(get("/scriptResult")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap())); // do we want an Empty map response or a "null" value ?
    }

    @Test
    void getAll1Result() throws Exception {
        // Mock Service
        List<ScriptResult> scriptResultList = new ArrayList<>();
        scriptResultList.add(createADummyScriptResult(1));
        given(scriptResultService.getAll()).willReturn(scriptResultList);
        // Request
        mvc.perform(get("/scriptResult")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$[\"_embedded\"]", hasSize(1)));
    }

    @Test
    void getAll3Results() throws Exception {
        // Mock Service
        List<ScriptResult> scriptResultList = new ArrayList<>();
        for (int i = 1; i <= 3; i++)
            scriptResultList.add(createADummyScriptResult(i));
        given(scriptResultService.getAll()).willReturn(scriptResultList);
        // Request
        mvc.perform(get("/scriptResult")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$[\"_embedded\"]", hasSize(3)));
    }

    @Test
    void getByRunIdNoResult() throws Exception {
        // Mock Service
        List<ScriptResult> scriptResultList = new ArrayList<>();
        given(scriptResultService.getByRunId("notTheSameId")).willReturn(scriptResultList);

        // Request
        mvc.perform(get("/scriptResult/notTheSameId")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    void getByRunId3Results() throws Exception {
        // Mock Service
        List<ScriptResult> scriptResultList = new ArrayList<>();
        for (int i = 1; i <= 3; i++)
            scriptResultList.add(createADummyScriptResult(i, "sameId"));
        given(scriptResultService.getByRunId("sameId")).willReturn(scriptResultList);

        // Request
        mvc.perform(get("/scriptResult/sameId")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$[\"_embedded\"]", hasSize(3)));
    }

    @Test
    void getByRunIdAndProcessIdSuccessful() throws Exception {
        // Mock Service
        Optional<ScriptResult> optionalScriptResult = Optional.of(createADummyScriptResult(1));
        given(scriptResultService.getByRunIdAndProcessId("sameId", 1L)).willReturn(optionalScriptResult);
        ScriptResult scriptResult = optionalScriptResult.get();
        // Request
        mvc.perform(get("/scriptResult/sameId/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.runID", is(scriptResult.getMetadataKey().getRunId())))
                .andExpect(jsonPath("$.processId", is((int) ((long) scriptResult.getMetadataKey().getProcessId()))))
                .andExpect(jsonPath("$.scriptId", is(scriptResult.getScriptId())))
                .andExpect(jsonPath("$.scriptName", is(scriptResult.getScriptName())))
                .andExpect(jsonPath("$.scriptVersion", is((int) ((long) scriptResult.getScriptVersion()))))
                .andExpect(jsonPath("$.environment", is(scriptResult.getEnvironment())))
                .andExpect(jsonPath("$.status", is(String.valueOf(scriptResult.getStatus()))))
                .andExpect(jsonPath("$.startTimestamp", is(scriptResult.getStartTimestamp() + ":00")))
                .andExpect(jsonPath("$.endTimestamp", is(scriptResult.getEndTimestamp() + ":00")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/scriptResult/" + scriptResult.getMetadataKey().getRunId() + "/" + scriptResult.getMetadataKey().getProcessId())));
    }

    @Test
    void getByRunIdAndProcessIdNoResultThrowException() {
        // Mock Service
        Optional<ScriptResult> optionalScriptResult = Optional.empty();
        given(scriptResultService.getByRunIdAndProcessId("Id", 1L)).willReturn(optionalScriptResult);

        assertThatThrownBy(() -> mvc.perform(get("/scriptResult/Id/1").contentType(MediaType.APPLICATION_JSON)))
                .hasCauseInstanceOf(MetadataDoesNotExistException.class);
    }

    ScriptResult createADummyScriptResult(int n, String runId) {
        String nString = String.valueOf(n);
        LocalDateTime now = LocalDateTime.now();
        int nMonth = now.getMonthValue();
        int nDay = now.getDayOfMonth();
        int nHours = now.getHour();
        int nMin = now.getMinute();

        return ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey("id" + runId, (long) n))
                .parentProcessId((long) n)
                .scriptId(nString)
                .scriptName("Script" + nString)
                .scriptVersion((long) n)
                .environment(nString)
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.of(2000, nMonth, nDay, nHours, nMin))
                .endTimestamp(LocalDateTime.of(2000, nMonth, nDay, nHours, nMin))
                .build();
    }

    ScriptResult createADummyScriptResult(int n) {
        return createADummyScriptResult(n, String.format("%s", n));
    }
}