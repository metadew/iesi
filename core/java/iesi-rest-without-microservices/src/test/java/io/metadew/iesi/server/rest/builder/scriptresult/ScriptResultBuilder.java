package io.metadew.iesi.server.rest.builder.scriptresult;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.script.result.ScriptResult;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import java.time.LocalDateTime;

public class ScriptResultBuilder {

    private final String runId;
    private final long processId;
    private Long parentProcessId;
    private String scriptName;
    private Long scriptVersion;
    private String environment;
    private ScriptRunStatus status;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
    private String securityGroupName;

    public ScriptResultBuilder(String runId, long processId) {
        this.runId = runId;
        this.processId = processId;
    }

    public static ScriptResult simpleScriptResult(long n, String runId) {
        return ScriptResult.builder()
                .scriptResultKey(new ScriptResultKey(runId, n))
                .parentProcessId(n)
                .scriptId(Long.toString(n))
                .scriptName(Long.toString(n))
                .securityGroupName("PUBLIC")
                .scriptVersion(n)
                .environment(Long.toString(n))
                .status(ScriptRunStatus.SUCCESS)
                .startTimestamp(LocalDateTime.now())
                .endTimestamp(LocalDateTime.now())
                .build();
    }

    public static ScriptResult simpleScriptResult(long n) {
        return simpleScriptResult(n, String.format("%s", n));
    }


    public ScriptResultBuilder scriptName(String scriptName) {
        this.scriptName = scriptName;
        return this;
    }

    public ScriptResultBuilder environment(String environment) {
        this.environment = environment;
        return this;
    }

    public ScriptResultBuilder scriptVersion(Long scriptVersion) {
        this.scriptVersion = scriptVersion;
        return this;
    }

    public ScriptResultBuilder securityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
        return this;
    }


    public ScriptResultBuilder parentProcessId(Long parentProcessId) {
        this.parentProcessId = parentProcessId;
        return this;
    }

    public ScriptResultBuilder status(ScriptRunStatus status) {
        this.status = status;
        return this;
    }

    public ScriptResultBuilder startTimestamp(LocalDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
        return this;
    }

    public ScriptResultBuilder endTimestamp(LocalDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
        return this;
    }

    public ScriptResult build() {
        return new ScriptResult(new ScriptResultKey(runId, processId),
                parentProcessId,
                IdentifierTools.getScriptIdentifier(scriptName),
                scriptName,
                scriptVersion,
                securityGroupName,
                environment,
                status,
                startTimestamp,
                endTimestamp
        );
    }


}
