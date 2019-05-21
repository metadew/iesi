package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.ActionParameter;
import org.apache.logging.log4j.Level;

import javax.sql.rowset.CachedRowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        return sql;
    }

    private String getParameterInsertStatements(String scriptName, long scriptVersionNumber) {
        String result = "";

        for (ActionParameter actionParameter : this.getAction().getParameters()) {
            ActionParameterConfiguration actionParameterConfiguration = new ActionParameterConfiguration(actionParameter, this.getFrameworkExecution());
            if (!result.equalsIgnoreCase(""))
                result += "\n";
            result += actionParameterConfiguration.getInsertStatement(scriptName, scriptVersionNumber, this.getAction().getName());
        }

        return result;
    }

    public Optional<Action> getAction(long actionId) {
        frameworkExecution.getFrameworkLog().log(MessageFormat.format(
                "Fetching action {0}.", actionId), Level.DEBUG);
        String queryAction = "select SCRIPT_ID, SCRIPT_VRS_NB, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, ITERATION_VAL, CONDITION_VAL, EXP_ERR_FL, STOP_ERR_FL from "
                + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository()
                .getTableNameByLabel("Actions")
                + " where ACTION_ID = " + actionId;
        CachedRowSet crsAction = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryAction, "reader");
        try {
            if (crsAction.size() == 0) {
                return Optional.empty();
            } else if (crsAction.size() > 1) {
                frameworkExecution.getFrameworkLog().log(MessageFormat.format(
                        "Found multiple implementations for action {0}. Returning first implementation", actionId), Level.DEBUG);
            }
            crsAction.next();
            // Get parameters
            String queryActionParameters = "select ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL from "
                    + this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().getTableNameByLabel("ActionParameters")
                    + " where ACTION_ID = " + actionId;
            CachedRowSet crsActionParameters = this.getFrameworkExecution().getMetadataControl().getDesignMetadataRepository().executeQuery(queryActionParameters, "reader");
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
                    crsAction.getString("ITERATION_VAL"),
                    crsAction.getString("CONDITION_VAL"),
                    crsAction.getString("EXP_ERR_FL"),
                    crsAction.getString("STOP_ERR_FL"),
                    actionParameters
            );
            crsActionParameters.close();
            crsAction.close();
            return Optional.of(action);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));
            this.frameworkExecution.getFrameworkLog().log("exception=" + e, Level.INFO);
            this.frameworkExecution.getFrameworkLog().log("exception.stacktrace=" + StackTrace, Level.DEBUG);

            return Optional.empty();
        }
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