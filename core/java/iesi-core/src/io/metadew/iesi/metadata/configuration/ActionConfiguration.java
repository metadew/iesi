package io.metadew.iesi.metadata.configuration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.ActionParameter;

public class ActionConfiguration {

	private Action action;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ActionConfiguration(Action action, FrameworkExecution frameworkExecution) {
		this.setAction(action);
		this.setFrameworkExecution(frameworkExecution);
	}

	public ActionConfiguration(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Insert
	public String getInsertStatement(String scriptName, long scriptVersionNumber, int actionNumber) {
		String sql = "";

		sql += "INSERT INTO "
				+ this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
				.getTableNameByLabel("Actions");
		sql += " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, EXP_ERR_FL, STOP_ERR_FL) ";
		sql += "VALUES ";
		sql += "(";
		sql += "(" + SQLTools.GetLookupIdStatement(
				this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
						.getTableNameByLabel("Scripts"),
				"SCRIPT_ID", "SCRIPT_NM", scriptName) + ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(scriptVersionNumber);
		sql += ",";
		sql += "(" + SQLTools.GetNextIdStatement(
				this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
						.getTableNameByLabel("Actions"),
				"ACTION_ID") + ")";
		sql += ",";
		sql += SQLTools.GetStringForSQL(actionNumber);
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getType());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getName());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getDescription());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getComponent());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getIteration());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getCondition());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getErrorExpected());
		sql += ",";
		sql += SQLTools.GetStringForSQL(this.getAction().getErrorStop());
		sql += ")";
		sql += ";";

		// add Parameters
		String sqlParameters = this.getParameterInsertStatements(scriptName, scriptVersionNumber);
		if (!sqlParameters.equals("")) {
			sql += "\n";
			sql += sqlParameters;
		}

		return sql;
	}

	private String getParameterInsertStatements(String scriptName, long scriptVersionNumber) {
		String result = "";

		for (ActionParameter actionParameter : this.getAction().getParameters()) {
			ActionParameterConfiguration actionParameterConfiguration = new ActionParameterConfiguration(actionParameter, this.getFrameworkExecution());
			if (!result.equals(""))
				result += "\n";
			result += actionParameterConfiguration.getInsertStatement(scriptName, scriptVersionNumber, this.getAction().getName());
		}

		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Action getAction(long actionId) {
		Action action = new Action();
		CachedRowSet crsAction = null;
		String queryAction = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, EXP_ERR_FL, STOP_ERR_FL from "
					+ this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
				.getTableNameByLabel("Actions")
					+ " where ACTION_ID = " + actionId;
		crsAction = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryAction, "reader");
		ActionParameterConfiguration actionParameterConfiguration = new ActionParameterConfiguration(this.getFrameworkExecution());
		try {
			while (crsAction.next()) {
				action.setId(actionId);
				action.setNumber(crsAction.getLong("ACTION_NB"));
				action.setType(crsAction.getString("ACTION_TYP_NM"));
				action.setName(crsAction.getString("ACTION_NM"));
				action.setDescription(crsAction.getString("ACTION_DSC"));
				action.setComponent(crsAction.getString("COMP_NM"));
				action.setIteration(crsAction.getString("ITERATION_VAL"));
				action.setCondition(crsAction.getString("CONDITION_VAL"));
				action.setErrorExpected(crsAction.getString("EXP_ERR_FL"));
				action.setErrorStop(crsAction.getString("STOP_ERR_FL"));
				
				// Get parameters
				CachedRowSet crsActionParameters = null;
				String queryActionParameters = "select ACTION_ID, ACTION_PAR_NM from "
						+ this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
						.getTableNameByLabel("ActionParameters")
						+ " where ACTION_ID = " + actionId;
				crsActionParameters = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryActionParameters, "reader");
				List<ActionParameter> actionParameterList = new ArrayList();
				while (crsActionParameters.next()) {
					actionParameterList
							.add(actionParameterConfiguration.getActionParameter(actionId, crsActionParameters.getString("ACTION_PAR_NM")));
				}
				action.setParameters(actionParameterList);
				crsActionParameters.close();

			}
			crsAction.close();
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));
		}
		return action;
	}

	// Getters and Setters
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
	

	
}