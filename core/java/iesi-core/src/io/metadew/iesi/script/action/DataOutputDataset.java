package io.metadew.iesi.script.action;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * This action prints a dataset for logging and debugging purposes
 *
 * @author Peter Billen
 */
public class DataOutputDataset {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation datasetName;
    private ActionParameterOperation datasetLabels;
    private ActionParameterOperation onScreen;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public DataOutputDataset() {

    }

    public DataOutputDataset(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        this.setDatasetName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
        this.setDatasetLabels(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "labels"));
        this.setOnScreen(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "onScreen"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("name")) {
                this.getDatasetName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("labels")) {
                this.getDatasetLabels().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("onscreen")) {
                this.getOnScreen().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("message", this.getDatasetName());
        this.getActionParameterOperationMap().put("labels", this.getDatasetLabels());
        this.getActionParameterOperationMap().put("onScreen", this.getOnScreen());
    }

    public boolean execute() {
        try {
            // do the magic
            System.out.println(this.getDatasetName().getValue());

            this.getActionExecution().getActionControl().increaseSuccessCount();

            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

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

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getOnScreen() {
        return onScreen;
    }

    public void setOnScreen(ActionParameterOperation onScreen) {
        this.onScreen = onScreen;
    }

    public ActionParameterOperation getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(ActionParameterOperation datasetName) {
        this.datasetName = datasetName;
    }

    public ActionParameterOperation getDatasetLabels() {
        return datasetLabels;
    }

    public void setDatasetLabels(ActionParameterOperation datasetLabels) {
        this.datasetLabels = datasetLabels;
    }

}