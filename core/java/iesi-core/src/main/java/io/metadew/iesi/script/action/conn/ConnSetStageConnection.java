package io.metadew.iesi.script.action.conn;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

public class ConnSetStageConnection {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation stageName;
    private ActionParameterOperation stageCleanup;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public ConnSetStageConnection() {

    }

    public ConnSetStageConnection(ExecutionControl executionControl,
                                  ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setStageName(new ActionParameterOperation(this.getExecutionControl(), this.getActionExecution(),
                this.getActionExecution().getAction().getType(), "stage"));
        this.setStageCleanup(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "cleanUp"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("stage")) {
                this.getStageName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("cleanup")) {
                this.getStageCleanup().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("stage", this.getStageName());
        this.getActionParameterOperationMap().put("cleanup", this.getStageCleanup());
    }

    //
    public boolean execute() throws InterruptedException {
        try {
            String stageName = convertStageName(getStageName().getValue());
            boolean cleanup = convertCleanup(getStageCleanup().getValue());
            return execute(stageName, cleanup);
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean execute(String stageName, boolean cleanup) throws InterruptedException {
        // Set the stage connection
        this.getExecutionControl().getExecutionRuntime().setStage(stageName, cleanup);

        // Increase the success count
        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    private String convertStageName(DataType stageName) {
        if (stageName instanceof Text) {
            return stageName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for stage name",
                    stageName.getClass()));
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
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for cleanup",
                    cleanup.getClass()));
            return false;
        }
    }

    // Getters and Setters
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