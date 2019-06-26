package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Script;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ActionParameterConfiguration {

    private ActionParameter actionParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ActionParameterConfiguration(ActionParameter actionParameter, FrameworkInstance frameworkInstance) {
        this.setActionParameter(actionParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ActionParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public String getInsertStatement(String scriptId, long scriptVersionNumber, String actionId, ActionParameter actionParameter) {
        return "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("ActionParameters") +
                " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(scriptId) + "," +
                scriptVersionNumber + "," +
                SQLTools.GetStringForSQL(actionId) + "," +
                SQLTools.GetStringForSQL(actionParameter.getName()) + "," +
                SQLTools.GetStringForSQL(actionParameter.getValue()) + ");";
    }

    // Insert
    public String getInsertStatement(Script script, Action action) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("ActionParameters");
        sql += " (SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(script.getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(script.getVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(action.getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getActionParameter().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(this.getActionParameter().getValue());
        sql += ")";
        sql += ";";

        return sql;
    }

    public ActionParameter getActionParameter(Script script, String actionId, String actionParameterName) {
        ActionParameter actionParameter = new ActionParameter();
        CachedRowSet crsActionParameter = null;
        String queryActionParameter = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                + this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("ActionParameters")
                + " where SCRIPT_ID = " + SQLTools.GetStringForSQL(script.getId()) + " and SCRIPT_VRS_NB = " + script.getVersion().getNumber()
                + " AND ACTION_ID = " + SQLTools.GetStringForSQL(actionId) + "' and ACTION_PAR_NM = '" + actionParameterName + "'";
        crsActionParameter = this.getFrameworkInstance().getMetadataControl().getDesignMetadataRepository()
                .executeQuery(queryActionParameter, "reader");
        try {
            while (crsActionParameter.next()) {
                actionParameter.setName(actionParameterName);
                actionParameter.setValue(crsActionParameter.getString("ACTION_PAR_VAL"));
            }
            crsActionParameter.close();
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
        }
        return actionParameter;
    }

    // Getters and Setters
    public ActionParameter getActionParameter() {
        return actionParameter;
    }

    public void setActionParameter(ActionParameter actionParameter) {
        this.actionParameter = actionParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}


}