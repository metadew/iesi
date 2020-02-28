package io.metadew.iesi.server.rest.resource.script_exection.dto;

import io.metadew.iesi.framework.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecution;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.server.rest.resource.script_execution.dto.ScriptExecutionDto;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ScriptExecutionDtoTest {

    @Test
    public void convertToEntityTest() {
        String scriptExecutionId = UUID.randomUUID().toString();
        String scriptExecutionRequestId = UUID.randomUUID().toString();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plus(1, ChronoUnit.MINUTES);
        ScriptExecution scriptExecution = new ScriptExecution(new ScriptExecutionKey(scriptExecutionId),
                new ScriptExecutionRequestKey(scriptExecutionRequestId),
                "runid",
                ScriptRunStatus.NEW,
                start,
                end);

        ScriptExecutionDto scriptExecutionDto = new ScriptExecutionDto(scriptExecutionId, scriptExecutionRequestId, "runid",
                ScriptRunStatus.NEW,
                start,
                end);

        assertEquals(scriptExecution, scriptExecutionDto.convertToEntity());
    }

}