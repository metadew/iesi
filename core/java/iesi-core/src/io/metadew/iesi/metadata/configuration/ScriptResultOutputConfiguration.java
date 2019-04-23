package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javax.sql.rowset.CachedRowSet;
import javax.swing.text.html.Option;

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
	public Optional<ScriptResultOutput> getScriptOutput(String runId, long processId, String scriptResultOutputName) {
		ScriptResultOutput scriptResultOutput = null;
		CachedRowSet crsScriptResultOutput;
		String queryScriptResultOutput = "select RUN_ID, PRC_ID, SCRIPT_ID, OUT_NM, OUT_VAL from " + this.getFrameworkExecution().getMetadataControl().getResultRepositoryConfiguration().getMetadataTableConfiguration().getTableName("ScriptOutputs")
				+ " where RUN_ID = '" + runId + "' and PRC_ID = " + processId + " and OUT_NM = '" + scriptResultOutputName + "'";
		crsScriptResultOutput = this.getFrameworkExecution().getMetadataControl().getResultRepositoryConfiguration().executeQuery(queryScriptResultOutput);
		try {
			while (crsScriptResultOutput.next()) {
				scriptResultOutput = new ScriptResultOutput(scriptResultOutputName,
						crsScriptResultOutput.getString("OUT_VAL"));
			}
			crsScriptResultOutput.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
			return Optional.empty();
		}
		return Optional.ofNullable(scriptResultOutput);
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