package io.metadew.iesi.metadata.definition.action.result;

import io.metadew.iesi.common.configuration.ScriptRunStatus;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.action.result.key.ActionResultKey;

import java.time.LocalDateTime;

public class ActionResult extends Metadata<ActionResultKey> {

	private Long scriptProcessId;
	private String actionName;
	private String environment;
	private ScriptRunStatus status;
	private LocalDateTime startTimestamp;
	private LocalDateTime endTimestamp;

	public ActionResult(String runId, Long processId, String actionId, Long scriptProcessId,  String actionName, String environment,
						ScriptRunStatus status, LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
		super(new ActionResultKey(runId, processId, actionId));
		this.scriptProcessId = scriptProcessId;
		this.actionName = actionName;
		this.environment = environment;
		this.status = status;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
	}
	public ActionResult(ActionResultKey actionResultKey, Long scriptProcessId, String actionName, String environment, ScriptRunStatus status,
						LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
		super(actionResultKey);
		this.scriptProcessId = scriptProcessId;
		this.actionName = actionName;
		this.environment = environment;
		this.status = status;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
	}

	// Getters and Setters
	public String getActionName() {
		return actionName;
	}

	public String getEnvironment() {
		return environment;
	}

	public ScriptRunStatus getStatus() {
		return status;
	}

	public void setStatus(ScriptRunStatus status) {
		this.status = status;
	}

	public LocalDateTime getStartTimestamp() {
		return startTimestamp;
	}

	public LocalDateTime getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(LocalDateTime endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public Long getScriptProcessId() {
		return scriptProcessId;
	}
}