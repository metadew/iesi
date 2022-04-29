package io.metadew.iesi.server.rest.executionrequest.script.dto;

import io.metadew.iesi.metadata.definition.execution.key.ExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestImpersonation;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestParameter;
import io.metadew.iesi.metadata.definition.execution.script.ScriptExecutionRequestStatus;
import io.metadew.iesi.metadata.definition.execution.script.ScriptNameExecutionRequest;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestImpersonationKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestKey;
import io.metadew.iesi.metadata.definition.execution.script.key.ScriptExecutionRequestParameterKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptExecutionRequestDtoTest {

    @Test
    void convertToEntityTest() {
        String executionRequestId = UUID.randomUUID().toString();
        String scriptExecutionRequestId = UUID.randomUUID().toString();
        ScriptNameExecutionRequest scriptNameExecutionRequest = new ScriptNameExecutionRequest(
                new ScriptExecutionRequestKey(scriptExecutionRequestId),
                new ExecutionRequestKey(executionRequestId),
                "tst",
                Stream.of(new ScriptExecutionRequestImpersonation(
                                new ScriptExecutionRequestImpersonationKey(DigestUtils.sha256Hex(scriptExecutionRequestId + "name1")),
                                new ScriptExecutionRequestKey(scriptExecutionRequestId),
                                new ImpersonationKey("name1")),
                        new ScriptExecutionRequestImpersonation(
                                new ScriptExecutionRequestImpersonationKey(DigestUtils.sha256Hex(scriptExecutionRequestId + "name2")),
                                new ScriptExecutionRequestKey(scriptExecutionRequestId),
                                new ImpersonationKey("name2")))
                        .collect(Collectors.toSet()),
                Stream.of(new ScriptExecutionRequestParameter(
                                new ScriptExecutionRequestParameterKey(DigestUtils.sha256Hex(scriptExecutionRequestId + "param1")),
                                new ScriptExecutionRequestKey(scriptExecutionRequestId),
                                "param1",
                                "value1"),
                        new ScriptExecutionRequestParameter(
                                new ScriptExecutionRequestParameterKey(DigestUtils.sha256Hex(scriptExecutionRequestId + "param2")),
                                new ScriptExecutionRequestKey(scriptExecutionRequestId),
                                "param2",
                                "value2")).collect(Collectors.toSet()),
                ScriptExecutionRequestStatus.NEW,
                "script",
                1L);

        ScriptExecutionRequestDto scriptExecutionRequestDto = new ScriptExecutionRequestDto(
                scriptExecutionRequestId,
                executionRequestId,
                "tst",
                Stream.of(
                        new ScriptExecutionRequestImpersonationDto("name1"),
                        new ScriptExecutionRequestImpersonationDto("name2")
                ).collect(Collectors.toSet()),
                Stream.of(
                        new ScriptExecutionRequestParameterDto("param1", "value1"),
                        new ScriptExecutionRequestParameterDto("param2", "value2")
                ).collect(Collectors.toSet()),
                ScriptExecutionRequestStatus.NEW,
                "script",
                1L,
                null,
                null,
                null);

        assertThat(scriptExecutionRequestDto.convertToEntity())
                .isEqualTo(scriptNameExecutionRequest);
    }

}