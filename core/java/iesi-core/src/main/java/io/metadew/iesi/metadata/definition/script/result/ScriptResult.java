package io.metadew.iesi.metadata.definition.script.result;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultKey;

import java.time.LocalDateTime;

public class ScriptResult extends Metadata<ScriptResultKey> {

	private final Long parentProcessId;
	private final String scriptId;
	private final String scriptName;
	private final Long scriptVersion;
	private final String environment;
	private String status;
	private LocalDateTime startTimestamp;
	private LocalDateTime endTimestamp;

	// Constructors
	public ScriptResult(ScriptResultKey scriptResultKey, Long parentProcessId, String scriptId, String scriptName,
						Long scriptVersion, String environment, String status, LocalDateTime startTimestamp,
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

	public ScriptResult(String runId, Long processId, Long parentProcessId, String scriptId, String scriptName, Long scriptVersion, String environment, String status, LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
		super(new ScriptResultKey(runId, processId));
		this.parentProcessId = parentProcessId;
		this.scriptId = scriptId;
		this.scriptName = scriptName;
		this.scriptVersion = scriptVersion;
		this.environment = environment;
		this.status = status;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
	}

	// Getters and Setters
	public String getScriptName() {
		return scriptName;
	}

	public String getScriptId() {
		return scriptId;
	}

	public Long getParentProcessId() {
		return parentProcessId;
	}

	public String getEnvironment() {
		return environment;
	}

	public String getStatus() {
		return status;
	}

	public LocalDateTime getStartTimestamp() {
		return startTimestamp;
	}

	public LocalDateTime getEndTimestamp() {
		return endTimestamp;
	}

	public Long getScriptVersion() {
		return scriptVersion;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStartTimestamp(LocalDateTime startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public void setEndTimestamp(LocalDateTime endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
}