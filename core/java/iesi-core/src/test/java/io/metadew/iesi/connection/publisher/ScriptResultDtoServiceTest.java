package io.metadew.iesi.connection.publisher;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptResultDtoServiceTest {

    @Test
    void deserializeTest() throws IOException {
        String data = "{\"runID\":\"runId\",\"processId\":0,\"parentProcessId\":0,\"scriptId\":\"scriptId\",\"scriptName\":\"scriptName\",\"scriptVersion\":0,\"environment\":\"environment\",\"status\":\"SUCCESS\",\"startTimestamp\":\"2020-09-11 17:31:35.120\",\"endTimestamp\":\"2020-09-11 17:31:36.120\"}";
        assertThat(ScriptResultDtoService.getInstance().deserialize(data))
                .isEqualTo(new ScriptResultDto(
                        "runId",
                        0L,
                        0L,
                        "scriptId",
                        "scriptName",
                        0L,
                        "environment",
                        ScriptRunStatus.SUCCESS,
                        LocalDateTime.of(LocalDate.of(2020, 9, 11), LocalTime.of(17, 31, 35, 120000000)),
                        LocalDateTime.of(LocalDate.of(2020, 9, 11), LocalTime.of(17, 31, 36, 120000000))));
    }

    @Test
    void serializeTest() throws IOException {
        ScriptResultDto scriptResultDto = new ScriptResultDto(
                "runId",
                0L,
                0L,
                "scriptId",
                "scriptName",
                0L,
                "environment",
                ScriptRunStatus.SUCCESS,
                LocalDateTime.of(LocalDate.of(2020, 9, 11), LocalTime.of(17, 31, 35, 120156123)),
                LocalDateTime.of(LocalDate.of(2020, 9, 11), LocalTime.of(17, 31, 36, 120156123)));
        assertThat(ScriptResultDtoService.getInstance().serialize(scriptResultDto))
                .isEqualTo("{\"runID\":\"runId\",\"processId\":0,\"parentProcessId\":0,\"scriptId\":\"scriptId\",\"scriptName\":\"scriptName\",\"scriptVersion\":0,\"environment\":\"environment\",\"status\":\"SUCCESS\",\"startTimestamp\":\"2020-09-11 17:31:35.120\",\"endTimestamp\":\"2020-09-11 17:31:36.120\"}");
    }
}
