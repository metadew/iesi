package io.metadew.iesi.script.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.connection.tools.SQLTools;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.util.HashMap;
import java.util.Iterator;
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
        String sql = "";

        sql += "INSERT INTO "
                + this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ScriptTraces");
        sql += " (RUN_ID, PRC_ID, PARENT_PRC_ID, SCRIPT_ID, SCRIPT_VRS_NB, SCRIPT_TYP_NM, SCRIPT_NM, SCRIPT_DSC) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(scriptExecution.getExecutionControl().getRunId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptExecution.getProcessId());
        sql += ",";
        sql += SQLTools.GetStringForSQL((parentScriptExecution == null ? 0 : parentScriptExecution.getProcessId()));
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptExecution.getScript().getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptExecution.getScript().getVersion().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptExecution.getScript().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptExecution.getScript().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptExecution.getScript().getDescription());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getParameterInsertStatements(scriptExecution);
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += "\n";
            sql += sqlParameters;
        }

        // Execute SQL
        this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().executeUpdate(sql);
    }

    private String getParameterInsertStatements(ScriptExecution scriptExecution) {
        String result = "";

        if (scriptExecution.getScript().getParameters() == null) return result;

        return result;
    }

    public void setExecution(ScriptExecution scriptExecution, ActionExecution actionExecution, HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        String sql = "";

        sql += "INSERT INTO " + this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionTraces");
        sql += " (RUN_ID, PRC_ID, SCRIPT_PRC_ID, ACTION_ID, ACTION_NB, ACTION_TYP_NM, ACTION_NM, ACTION_DSC, COMP_NM, EXP_ERR_FL, STOP_ERR_FL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(actionExecution.getExecutionControl().getRunId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getProcessId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(scriptExecution.getProcessId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getAction().getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getAction().getNumber());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getAction().getType());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getAction().getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getAction().getDescription());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getAction().getComponent());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getAction().getErrorExpected());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getAction().getErrorStop());
        sql += ")";
        sql += ";";

        // add Parameters
        String sqlParameters = this.getOperationInsertStatements(actionExecution, actionParameterOperationMap);
        if (!sqlParameters.equalsIgnoreCase("")) {
            sql += sqlParameters;
        }

        // Execute SQL
        this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().executeUpdate(sql);
    }

    @SuppressWarnings("rawtypes")
    private String getOperationInsertStatements(ActionExecution actionExecution, HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        String result = "";

        if (actionParameterOperationMap == null) return result;

        Iterator iterator = actionParameterOperationMap.entrySet().iterator();
        ObjectMapper objectMapper = new ObjectMapper();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            ActionParameterOperation actionParameterOperation = objectMapper.convertValue(pair.getValue(), ActionParameterOperation.class);

            // Handle null values when parameter has not been set
            if (actionParameterOperation == null) continue;

            String sql = this.getOperationInsertStatement(actionExecution, actionParameterOperation);
            if (!sql.equalsIgnoreCase("")) {
                result += "\n";
                result += sql;
            }
            iterator.remove(); // avoids a ConcurrentModificationException
        }

        return result;
    }

    private String getOperationInsertStatement(ActionExecution actionExecution, ActionParameterOperation actionParameterOperation) {
        String sql = "";

        sql += "INSERT INTO "
                + this.getFrameworkExecution().getMetadataControl().getTraceMetadataRepository().getTableNameByLabel("ActionParameterTraces");
        sql += " (RUN_ID, PRC_ID, ACTION_ID, ACTION_PAR_NM, ACTION_PAR_VAL) ";
        sql += "VALUES ";
        sql += "(";
        sql += SQLTools.GetStringForSQL(actionExecution.getExecutionControl().getRunId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getProcessId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionExecution.getAction().getId());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionParameterOperation.getName());
        sql += ",";
        sql += SQLTools.GetStringForSQL(actionParameterOperation.getValue() != null ? actionParameterOperation.getValue().toString() : null);
        sql += ")";
        sql += ";";
        
        return sql;
    }


    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }


}