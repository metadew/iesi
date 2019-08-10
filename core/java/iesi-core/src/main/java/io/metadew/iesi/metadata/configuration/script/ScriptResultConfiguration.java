package io.metadew.iesi.metadata.configuration.script;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.action.ActionResultConfiguration;
import io.metadew.iesi.metadata.definition.script.ScriptResult;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ScriptResultConfiguration {

	private final ActionResultConfiguration actionResultConfiguration;
	private final ScriptResultOutputConfiguration scriptResultOutputConfiguration;

	// Constructors
	public ScriptResultConfiguration() {
		this.actionResultConfiguration = new ActionResultConfiguration();
		this.scriptResultOutputConfiguration =  new ScriptResultOutputConfiguration();
	}

	public ScriptResult getScript(String runId) {
		ScriptResult scriptResult = new ScriptResult();
		String queryScript = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
				+ MetadataControl.getInstance().getResultMetadataRepository()
						.getTableNameByLabel("ScriptResults")
				+ " where RUN_ID = '" + runId + "' and PARENT_PRC_ID = 0";
		CachedRowSet crsScriptResult = MetadataControl.getInstance().getResultMetadataRepository()
				.executeQuery(queryScript, "reader");
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


	public List<ScriptResult> getChildScripts(String runId) {
		List<ScriptResult> scriptResults = new ArrayList<>();
		String queryScript = "select RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_NM, SCRIPT_VRS_NB, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
				+ MetadataControl.getInstance().getResultMetadataRepository()
						.getTableNameByLabel("ScriptResults")
				+ " where RUN_ID = '" + runId + "' and PARENT_PRC_ID > 0";
		CachedRowSet crsScriptResult = MetadataControl.getInstance().getResultMetadataRepository()
				.executeQuery(queryScript, "reader");
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

}