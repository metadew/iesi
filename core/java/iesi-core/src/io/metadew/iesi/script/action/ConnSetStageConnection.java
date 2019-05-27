package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

public class ConnSetStageConnection {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation stageName;
    private ActionParameterOperation stageCleanup;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public ConnSetStageConnection() {

    }

    public ConnSetStageConnection(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                  ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setStageName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "stage"));
        this.setStageCleanup(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "cleanUp"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("stage")) {
                this.getStageName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("cleanup")) {
                this.getStageCleanup().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("stage", this.getStageName());
        this.getActionParameterOperationMap().put("cleanup", this.getStageCleanup());
    }

    //
    public boolean execute() {
        try {
            String stageName = convertStageName(getStageName().getValue());
            boolean cleanup = convertCleanup(getStageCleanup().getValue());
            return execute(stageName, cleanup);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean execute(String stageName, boolean cleanup) {
        // Set the stage connection
        try {
            this.getExecutionControl().getExecutionRuntime().setStage(stageName, cleanup);
        } catch (Exception e) {
            throw new RuntimeException("conn.stage.error");
        }

        // Increase the success count
        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    private String convertStageName(DataType stageName) {
        if (stageName instanceof Text) {
            return stageName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for stage name",
                    stageName.getClass()), Level.WARN);
            return stageName.toString();
        }
    }

    private boolean convertCleanup(DataType cleanup) {
        // TODO: make optional
        if (cleanup == null) {
            return false;
        }
        if (cleanup instanceof Text) {
            return cleanup.toString().equalsIgnoreCase("y");
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for cleanup",
                    cleanup.getClass()), Level.WARN);
            return false;
        }
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public ActionParameterOperation getStageName() {
        return stageName;
    }

    public void setStageName(ActionParameterOperation stageName) {
        this.stageName = stageName;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getStageCleanup() {
        return stageCleanup;
    }

    public void setStageCleanup(ActionParameterOperation stageCleanup) {
        this.stageCleanup = stageCleanup;
    }

}