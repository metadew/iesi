package io.metadew.iesi.metadata.definition;

import java.util.List;

public class ActionResult {

	private Long processId;
	private Long scriptProcessId;
	private String id;
	private String name;
	private String environment;
	private String status;
	private String start;
	private String end;
	private List<ActionResultOutput> outputs;

	// Constructors
	public ActionResult() {
		
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
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

	public Long getScriptProcessId() {
		return scriptProcessId;
	}

	public void setScriptProcessId(Long scriptProcessId) {
		this.scriptProcessId = scriptProcessId;
	}

	public List<ActionResultOutput> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<ActionResultOutput> outputs) {
		this.outputs = outputs;
	}

}