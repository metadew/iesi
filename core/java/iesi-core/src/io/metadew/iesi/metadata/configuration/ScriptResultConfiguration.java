package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ScriptResult;

public class ScriptResultConfiguration {

	private FrameworkInstance frameworkInstance;

	// Constructors
	public ScriptResultConfiguration(FrameworkInstance frameworkInstance) {
		this.setFrameworkInstance(frameworkInstance);
	}

	public ScriptResult getScript(String runId) {
		ScriptResult scriptResult = new ScriptResult();
		CachedRowSet crsScriptResult = null;
		String queryScript = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
				+ this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository()
						.getTableNameByLabel("ScriptResults")
				+ " where RUN_ID = '" + runId + "' and PARENT_PRC_ID = 0";
		crsScriptResult = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
				.executeQuery(queryScript, "reader");
		try {
			while (crsScriptResult.next()) {
				scriptResult.setRunId(runId);
				scriptResult.setProcessId(crsScriptResult.getLong("PRC_ID"));
				scriptResult.setParentProcessId(crsScriptResult.getLong("PARENT_PRC_ID"));
				scriptResult.setId(crsScriptResult.getString("SCRIPT_ID"));
				scriptResult.setName(crsScriptResult.getString("SCRIPT_NM"));
				scriptResult.setVersion(crsScriptResult.getLong("SCRIPT_VRS_NB"));
				scriptResult.setEnvironment(crsScriptResult.getString("ENV_NM"));
				scriptResult.setStatus(crsScriptResult.getString("ST_NM"));
				scriptResult.setStart(crsScriptResult.getString("STRT_TMS"));
				scriptResult.setEnd(crsScriptResult.getString("END_TMS"));
			}
			crsScriptResult.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		if (scriptResult.getName() == null || scriptResult.getName().equalsIgnoreCase("")) {
			throw new RuntimeException("scriptresult.error.notfound");
		}

		return scriptResult;
	}

	// Getters and Setters
	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}