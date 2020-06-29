package io.metadew.iesi.metadata.definition.script.result;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptResult extends Metadata<ScriptResultKey> {

    private final Long parentProcessId;
    private final String scriptId;
    private final String scriptName;
    private final Long scriptVersion;
    private final String environment;
    private ScriptRunStatus status;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;

    @Builder
    public ScriptResult(ScriptResultKey scriptResultKey, Long parentProcessId, String scriptId, String scriptName,
                        Long scriptVersion, String environment, ScriptRunStatus status, LocalDateTime startTimestamp,
                        LocalDateTime endTimestamp) {
        super(scriptResultKey);
        this.parentProcessId = parentProcessId;
        this.scriptId = scriptId;
        this.scriptName = scriptName;
        this.scriptVersion = scriptVersion;
        this.environment = environment;
        this.status = status;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

}