package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.metadata.tools.IdentifierTools;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActionConfiguration {

    private Action action;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ActionConfiguration(Action action, FrameworkInstance frameworkInstance) {
        this.setAction(action);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ActionConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Insert
    public String getInsertStatement(Script script, int actionNumber) {
        String sql = "";
        long scriptVersionNumber = script.getVersion().getNumber();
        this.getAction().setId(IdentifierTools.getActionIdentifier(this.getAction().getName()));

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("Actions");
        sql += " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) ";
        sql += "VALUES (";
        sql += SQLTools.GetStringForSQL(script.getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptVersionNumber);
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getAction().getId());
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
        sql += SQLTools.GetStringForSQL(this.getAction().getRetries());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getAction().getErrorExpected());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getAction().getErrorStop());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(script);
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(Script script) {
        StringBuilder result = new StringBuilder();

        for (ActionParameter actionParameter : this.getAction().getParameters()) {
            ActionParameterConfiguration actionParameterConfiguration = new ActionParameterConfiguration(actionParameter, this.getFrameworkInstance());
            if (!result.toString().equalsIgnoreCase(""))
                result.append("\n");
            result.append(actionParameterConfiguration.getInsertStatement(script, this.getAction()));
        }

        return result.toString();
    }


    public Optional<Action> getAction(Script script, String actionId) {
        return getAction(script.getId(), script.getVersion().getNumber(), actionId);
    }

    public Optional<Action> getAction(String scriptId, long scriptVersionNumber, String actionId) {
    	// TODO fix logging
    	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Fetching action {0}.", actionId), Level.DEBUG);
        String queryAction = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, EXP_ERR_FL, STOP_ERR_FL, RETRIES_VAL from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("Actions")
                + " where ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
        CachedRowSet crsAction = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryAction, "reader");
        try {
            if (crsAction.size() == 0) {
                return Optional.empty();
            } else if (crsAction.size() > 1) {
                // TODO fix logging
            	//frameworkExecution.getFrameworkLog().log(MessageFormat.format("Found multiple implementations for action {0}. Returning first implementation", actionId), Level.DEBUG);
            }
            crsAction.next();
            // Get parameters
            String queryActionParameters = "select ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                    + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ActionParameters")
                    + " where ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + " AND SCRIPT_ID = " + SQLTools.GetStringForSQL(scriptId) + " AND SCRIPT_VRS_NB = '" + scriptVersionNumber + "'";
            CachedRowSet crsActionParameters = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository().executeQuery(queryActionParameters, "reader");
            List<ActionParameter> actionParameters = new ArrayList<>();
            while (crsActionParameters.next()) {
                actionParameters.add(new ActionParameter(crsActionParameters.getString("ACTION_PAR_NM"),
                        crsActionParameters.getString("ACTION_PAR_VAL")));
            }
            Action action = new Action(actionId,
                    crsAction.getLong("ACTION_NB"),
                    crsAction.getString("ACTION_TYP_NM"),
                    crsAction.getString("ACTION_NM"),
                    crsAction.getString("ACTION_DSC"),
                    crsAction.getString("COMP_NM"),
                    crsAction.getString("CONDITION_VAL"),
                    crsAction.getString("ITERATION_VAL"),
                    crsAction.getString("EXP_ERR_FL"),
                    crsAction.getString("STOP_ERR_FL"),
                    crsAction.getString("RETRIES_VAL"),
                    actionParameters
            );
            crsActionParameters.close();
            crsAction.close();
            return Optional.of(action);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            //TODO fix logging
            //this.frameworkExecution.getFrameworkLog().log("exception=" + e, Level.INFO);
            //this.frameworkExecution.getFrameworkLog().log("exception.stacktrace=" + StackTrace, Level.DEBUG);

            return Optional.empty();
        }
    }

    public String getInsertStatement(String scriptId, long scriptVersionNumber, Action action) {
        ActionParameterConfiguration actionParameterConfiguration = new ActionParameterConfiguration(this.getFrameworkInstance());
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO ").append(this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("Actions"));
        sql.append(" (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, RETRIES_VAL, EXP_ERR_FL, STOP_ERR_FL) VALUES (");
        sql.append(SQLTools.GetStringForSQL(scriptId)).append(",");
        sql.append(SQLTools.GetStringForSQL(scriptVersionNumber)).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getId())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getNumber())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getType())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getName())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getDescription())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getComponent())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getIteration())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getCondition())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getRetries())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getErrorExpected())).append(",");
        sql.append(SQLTools.GetStringForSQL(action.getErrorStop())).append(");");

        for (ActionParameter actionParameter : action.getParameters()) {
            sql.append(actionParameterConfiguration.getInsertStatement(scriptId, scriptVersionNumber, action.getId(), actionParameter));
            sql.append("\n");
        }
        return sql.toString();
    }

    // Getters and Setters
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}
}