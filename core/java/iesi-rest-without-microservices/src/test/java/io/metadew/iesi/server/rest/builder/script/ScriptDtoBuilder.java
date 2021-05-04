package io.metadew.iesi.server.rest.builder.script;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.server.rest.script.dto.ScriptDto;
import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.action.ActionParameterDto;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptExecutionDto;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptExecutionInformation;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.parameter.ScriptParameterDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScriptDtoBuilder {
    public static ScriptDto simpleScriptDto(String name, long n) {
        return ScriptDto.builder()
                .name(name)
                .description(name + " desc")
                .securityGroupName("PUBLIC")
                .version(new ScriptVersionDto(n, name + " " + Long.toString(n),"NA"))
                .parameters(
                        Stream.of(
                                new ScriptParameterDto("Param1", Long.toString(n)),
                                new ScriptParameterDto("Param2", Long.toString(n))
                        ).collect(Collectors.toSet())
                )
                .actions(
                        Stream.of(
                                ActionDto.builder()
                                        .number(n)
                                        .name("Action1")
                                        .type("Dummy")
                                        .description("DummyAction")
                                        .component("DummyComponent")
                                        .condition("DummyCondition")
                                        .iteration("DummyIteration")
                                        .errorExpected(false)
                                        .errorStop(false)
                                        .retries(0)
                                        .parameters(
                                                Stream.of(
                                                        new ActionParameterDto("ActionParameter1", "ActionParameterValue1"),
                                                        new ActionParameterDto("ActionParameter2", "ActionParameterValue2")
                                                ).collect(Collectors.toSet())
                                        )
                                        .build()
                        ).collect(Collectors.toSet())
                )
                .labels(
                        Stream.of(
                                new ScriptLabelDto("Label1", "Label1Value"),
                                new ScriptLabelDto("Label2", "Label2Value")
                        ).collect(Collectors.toSet())
                )
                .scriptExecutionInformation(new ScriptExecutionInformation(
                                2L,
                                Stream.of(
                                        new ScriptExecutionDto(
                                                "runId1",
                                                "test_env",
                                                ScriptRunStatus.SUCCESS,
                                                LocalDateTime.now().minusMinutes(2),
                                                LocalDateTime.now()
                                        ),
                                        new ScriptExecutionDto(
                                                "runId2",
                                                "test_env",
                                                ScriptRunStatus.SUCCESS,
                                                LocalDateTime.now(),
                                                LocalDateTime.now().plusMinutes(2)
                                        )
                                ).collect(Collectors.toList())
                        )
                )
                .deletedAt("NA")
                .build();
    }
}
