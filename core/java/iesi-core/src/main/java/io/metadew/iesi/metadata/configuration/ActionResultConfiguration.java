package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ActionResult;

public class ActionResultConfiguration {

	private FrameworkInstance frameworkInstance;

	// Constructors
	public ActionResultConfiguration(FrameworkInstance frameworkInstance) {
		this.setFrameworkInstance(frameworkInstance);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ActionResult> getActions(String runId) {
		List<ActionResult> actionResults = new ArrayList();
		CachedRowSet crsActionResults = null;
		String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
				+ this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository()
						.getTableNameByLabel("ActionResults")
				+ " where RUN_ID = '" + runId + "' order by PRC_ID asc, STRT_TMS asc";
		System.out.println(query);
		crsActionResults = this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository()
				.executeQuery(query, "reader");
		ActionResultOutputConfiguration actionResultOutputConfiguration = new ActionResultOutputConfiguration(this.getFrameworkInstance());
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ActionResult> getActions(String runId, Long scriptProcessId) {
		List<ActionResult> actionResults = new ArrayList();
		CachedRowSet crsActionResults = null;
		String query = "select RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NM, ENV_NM, ST_NM, STRT_TMS, END_TMS from "
				+ this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository()
						.getTableNameByLabel("ActionResults")
				+ " where RUN_ID = '" + runId + "' and SCRIPT_PRC_ID = " + scriptProcessId + " order by PRC_ID asc, STRT_TMS asc";
		System.out.println(query);
		crsActionResults = this.getFrameworkInstance().getMetadataControl().getResultMetadataRepository()
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

	// Getters and Setters
	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}