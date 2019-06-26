package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
		crsScriptResult = this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository()
				.executeQuery(queryScript, "reader");
		ActionResultConfiguration actionResultConfiguration = new ActionResultConfiguration(this.getFrameworkInstance());
		ScriptResultOutputConfiguration scriptResultOutputConfiguration =  new ScriptResultOutputConfiguration(this.getFrameworkInstance());
		try {
			while (crsScriptResult.next()) {
				scriptResult.setRunId(runId);
				Long processId = crsScriptResult.getLong("PRC_ID");
				scriptResult.setProcessId(processId);
				scriptResult.setParentProcessId(crsScriptResult.getLong("PARENT_PRC_ID"));
				scriptResult.setId(crsScriptResult.getString("SCRIPT_ID"));
				scriptResult.setName(crsScriptResult.getString("SCRIPT_NM"));
				scriptResult.setVersion(crsScriptResult.getLong("SCRIPT_VRS_NB"));
				scriptResult.setEnvironment(crsScriptResult.getString("ENV_NM"));
				scriptResult.setStatus(crsScriptResult.getString("ST_NM"));
				scriptResult.setStart(crsScriptResult.getString("STRT_TMS"));
				scriptResult.setEnd(crsScriptResult.getString("END_TMS"));
				scriptResult.setScripts(this.getChildScripts(runId));
				scriptResult.setActions(actionResultConfiguration.getActions(runId));
				scriptResult.setOutputs(scriptResultOutputConfiguration.getScriptResultOutputs(runId, processId));
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


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ScriptResult> getChildScripts(String runId) {
		List<ScriptResult> scriptResults = new ArrayList();
		CachedRowSet crsScriptResult = null;
		String queryScript = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
				+ this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository()
						.getTableNameByLabel("ScriptResults")
				+ " where RUN_ID = '" + runId + "' and PARENT_PRC_ID > 0";
		crsScriptResult = this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository()
				.executeQuery(queryScript, "reader");
		ActionResultConfiguration actionResultConfiguration = new ActionResultConfiguration(this.getFrameworkInstance());
		ScriptResultOutputConfiguration scriptResultOutputConfiguration =  new ScriptResultOutputConfiguration(this.getFrameworkInstance());
		try {
			while (crsScriptResult.next()) {
				ScriptResult scriptResult = new ScriptResult();
				scriptResult.setRunId(runId);
				Long processId = crsScriptResult.getLong("PRC_ID");
				scriptResult.setProcessId(processId);
				scriptResult.setParentProcessId(crsScriptResult.getLong("PARENT_PRC_ID"));
				scriptResult.setId(crsScriptResult.getString("SCRIPT_ID"));
				scriptResult.setName(crsScriptResult.getString("SCRIPT_NM"));
				scriptResult.setVersion(crsScriptResult.getLong("SCRIPT_VRS_NB"));
				scriptResult.setEnvironment(crsScriptResult.getString("ENV_NM"));
				scriptResult.setStatus(crsScriptResult.getString("ST_NM"));
				scriptResult.setStart(crsScriptResult.getString("STRT_TMS"));
				scriptResult.setEnd(crsScriptResult.getString("END_TMS"));
				scriptResult.setOutputs(scriptResultOutputConfiguration.getScriptResultOutputs(runId, processId));
				scriptResult.setActions(actionResultConfiguration.getActions(runId, processId));
				
				scriptResults.add(scriptResult);
			}
			crsScriptResult.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		return scriptResults;
	}

	// Getters and Setters
	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}