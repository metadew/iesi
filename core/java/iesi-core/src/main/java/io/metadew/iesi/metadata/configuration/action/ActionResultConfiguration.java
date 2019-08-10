package io.metadew.iesi.metadata.configuration.action;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.action.ActionResult;
import io.metadew.iesi.metadata.execution.MetadataControl;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ActionResultConfiguration {

	private final ActionResultOutputConfiguration actionResultOutputConfiguration;

	// Constructors
	public ActionResultConfiguration() {
		this.actionResultOutputConfiguration = new ActionResultOutputConfiguration();

	}

	public List<ActionResult> getActions(String runId) {
		List<ActionResult> actionResults = new ArrayList<>();
		String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
				+ MetadataControl.getInstance().getResultMetadataRepository()
						.getTableNameByLabel("ActionResults")
				+ " where RUN_ID = '" + runId + "' order by PRC_ID asc, STRT_TMS asc";
		CachedRowSet crsActionResults = MetadataControl.getInstance().getResultMetadataRepository()
				.executeQuery(query, "reader");
		try {
			while (crsActionResults.next()) {
				ActionResult actionResult = new ActionResult();
				Long processId = crsActionResults.getLong("PRC_ID");
				actionResult.setProcessId(processId);
				actionResult.setScriptProcessId(crsActionResults.getLong("SCRIPT_PRC_ID"));
				actionResult.setId(crsActionResults.getString("ACTION_ID"));
				actionResult.setName(crsActionResults.getString("ACTION_NM"));
				actionResult.setEnvironment(crsActionResults.getString("ENV_NM"));
				actionResult.setStatus(crsActionResults.getString("ST_NM"));
				actionResult.setStart(crsActionResults.getString("STRT_TMS"));
				actionResult.setEnd(crsActionResults.getString("END_TMS"));
				actionResult.setOutputs(actionResultOutputConfiguration.getActionResultOutputs(runId, processId));
				actionResults.add(actionResult);
			}
			crsActionResults.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		if (actionResults.size() == 0) {
			throw new RuntimeException("actionresult.error.empty");
		}

		return actionResults;
	}
	
	public List<ActionResult> getActions(String runId, Long scriptProcessId) {
		List<ActionResult> actionResults = new ArrayList<>();
		String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
				+ MetadataControl.getInstance().getResultMetadataRepository()
						.getTableNameByLabel("ActionResults")
				+ " where RUN_ID = '" + runId + "' and SCRIPT_PRC_ID = " + scriptProcessId + " order by PRC_ID asc, STRT_TMS asc";
		CachedRowSet crsActionResults = MetadataControl.getInstance().getResultMetadataRepository()
				.executeQuery(query, "reader");
		try {
			while (crsActionResults.next()) {
				ActionResult actionResult = new ActionResult();
				actionResult.setProcessId(crsActionResults.getLong("PRC_ID"));
				actionResult.setScriptProcessId(crsActionResults.getLong("SCRIPT_PRC_ID"));
				actionResult.setId(crsActionResults.getString("ACTION_ID"));
				actionResult.setName(crsActionResults.getString("ACTION_NM"));
				actionResult.setEnvironment(crsActionResults.getString("ENV_NM"));
				actionResult.setStatus(crsActionResults.getString("ST_NM"));
				actionResult.setStart(crsActionResults.getString("STRT_TMS"));
				actionResult.setEnd(crsActionResults.getString("END_TMS"));
				actionResults.add(actionResult);
			}
			crsActionResults.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}

		if (actionResults.size() == 0) {
			throw new RuntimeException("actionresult.error.empty");
		}

		return actionResults;
	}

}