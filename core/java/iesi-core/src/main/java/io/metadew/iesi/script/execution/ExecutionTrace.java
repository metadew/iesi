package io.metadew.iesi.script.execution;

import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for storing all trace information that is applicable during a script execution
 *
 * @author peter.billen
 */
public class ExecutionTrace {

    private FrameworkExecution frameworkExecution;

    // Constructors
    public ExecutionTrace(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Insert
    public void setExecution(ScriptExecution scriptExecution, ScriptExecution parentScriptExecution) {
        List<String> queries = new ArrayList<>();

        String sql = "";

        queries.add("INSERT INTO "
                + this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptTraces") +
                " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC) VALUES (" +
                SQLTools.GetStringForSQL(scriptExecution.getExecutionControl().getRunId()) + "," +
                SQLTools.GetStringForSQL(scriptExecution.getProcessId()) + "," +
                SQLTools.GetStringForSQL((parentScriptExecution == null ? 0 : parentScriptExecution.getProcessId())) + "," +
                SQLTools.GetStringForSQL(scriptExecution.getScript().getId()) + "," +
                SQLTools.GetStringForSQL(scriptExecution.getScript().getVersion().getNumber()) + "," +
                SQLTools.GetStringForSQL(scriptExecution.getScript().getType()) + "," +
                SQLTools.GetStringForSQL(scriptExecution.getScript().getName()) + "," +
                SQLTools.GetStringForSQL(scriptExecution.getScript().getDescription()) + ");");



        // add Parameters
        queries.addAll(this.getParameterInsertStatements(scriptExecution));

        // Execute SQL
        this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().executeBatch(queries);
    }

    private List<String> getParameterInsertStatements(ScriptExecution scriptExecution) {
        // TODO
        List<String> queries = new ArrayList<>();
        if (scriptExecution.getScript().getParameters() == null) return queries;
        return queries;
    }

    public void setExecution(ScriptExecution scriptExecution, ActionExecution actionExecution, HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        List<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionTraces") +
                " (RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, EXP_ERR_FL, STOP_ERR_FL) VALUES (" +
                SQLTools.GetStringForSQL(actionExecution.getExecutionControl().getRunId()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getProcessId()) + "," +
                SQLTools.GetStringForSQL(scriptExecution.getProcessId()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getAction().getId()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getAction().getNumber()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getAction().getType()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getAction().getName()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getAction().getDescription()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getAction().getComponent()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getAction().getErrorExpected()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getAction().getErrorStop()) + ");");

        // add Parameters
        queries.addAll(getOperationInsertStatements(actionExecution, actionParameterOperationMap));

        // Execute SQL
        this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().executeBatch(queries);
    }

    private List<String> getOperationInsertStatements(ActionExecution actionExecution, HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        List<String> queries = new ArrayList<>();
        if (actionParameterOperationMap == null) return queries;

        for (Map.Entry<String, ActionParameterOperation> actionParameterOperationEntry : actionParameterOperationMap.entrySet()) {
            if (actionParameterOperationEntry.getValue() == null) continue;
            queries.add(this.getOperationInsertStatement(actionExecution, actionParameterOperationEntry.getValue()));
        }

        return queries;
    }

    private String getOperationInsertStatement(ActionExecution actionExecution, ActionParameterOperation actionParameterOperation) {
        return "INSERT INTO "
                + this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterTraces") +
                " (RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) VALUES (" +
                SQLTools.GetStringForSQL(actionExecution.getExecutionControl().getRunId()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getProcessId()) + "," +
                SQLTools.GetStringForSQL(actionExecution.getAction().getId()) + "," +
                SQLTools.GetStringForSQL(actionParameterOperation.getName()) + "," +
                SQLTools.GetStringForSQL(actionParameterOperation.getValue() != null ? actionParameterOperation.getValue().toString() : null) +
                ");";
    }


    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }


}