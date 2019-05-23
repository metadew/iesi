package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.metadata.tools.IdentifierTools;

public class ScriptResult {

	private String runId;
	private Long processId;
	private Long parentProcessId;
	private String id;
	private String name;
	private Long version;
	private String environment;
	private String status;
	private String start;
	private String end;

	// Constructors
	public ScriptResult() {
		
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		if (id == null) this.id = IdentifierTools.getScriptIdentifier(this.getName());
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public Long getParentProcessId() {
		return parentProcessId;
	}

	public void setParentProcessId(Long parentProcessId) {
		this.parentProcessId = parentProcessId;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}


}