package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ScriptResultOutput;

public class ScriptResultOutputConfiguration {

	private String runId;
	private ScriptResultOutput scriptResultOutput;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ScriptResultOutputConfiguration(String runId, ScriptResultOutput scriptResultOutput, FrameworkExecution frameworkExecution) {
		this.setRunId(runId);
		this.setScriptResultOutput(scriptResultOutput);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ScriptResultOutputConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public ScriptResultOutput getScriptOutput(String runId, long processId, String scriptResultOutputName) {
		ScriptResultOutput scriptResultOutput = new ScriptResultOutput();
		CachedRowSet crsScriptResultOutput = null;
		String queryScriptResultOutput = "select RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL from " + this.getFrameworkExecution().getMetadataControl().getResultMetadataRepository().getTableNameByLabel("ScriptOutputs")
				+ " where RUN_ID = '" + runId + "' and PRC_ID = " + processId + " and OUT_NM = '" + scriptResultOutputName + "'";
		crsScriptResultOutput = this.getFrameworkExecution().getMetadataControl().getResultMetadataRepository().executeQuery(queryScriptResultOutput, "reader");
		try {
			while (crsScriptResultOutput.next()) {
				scriptResultOutput.setName(scriptResultOutputName);
				scriptResultOutput.setValue(crsScriptResultOutput.getString("OUT_VAL"));
			}
			crsScriptResultOutput.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return scriptResultOutput;
	}

	// Getters and Setters
	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public ScriptResultOutput getScriptResultOutput() {
		return scriptResultOutput;
	}

	public void setScriptResultOutput(ScriptResultOutput scriptResultOutput) {
		this.scriptResultOutput = scriptResultOutput;
	}

}