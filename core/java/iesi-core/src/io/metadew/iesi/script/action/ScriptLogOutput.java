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


/**
 * This action stores an output value as part of the script results
 *
 * @author Peter Billen
 */
public class ScriptLogOutput {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation outputName;
    private ActionParameterOperation outputValue;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public ScriptLogOutput() {

    }

    public ScriptLogOutput(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        this.setOutputName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
        this.setOutputValue(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "value"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("name")) {
                this.getOutputName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("value")) {
                this.getOutputValue().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("name", this.getOutputName());
        this.getActionParameterOperationMap().put("value", this.getOutputValue());
    }

    public boolean execute() {
        try {
            String outputName = convertOutputName(getOutputName().getValue());
            String outputValue = convertOutputValue(getOutputValue().getValue());
            // log the output in the script
            this.getActionExecution().getScriptExecution().getExecutionControl().logExecutionOutput(
                    this.getActionExecution().getScriptExecution(), outputName,
                    outputValue);

            // Log the output in the action as well
            this.getActionExecution().getActionControl().logOutput("output.name", outputName);
            this.getActionExecution().getActionControl().logOutput("output.value", outputValue);

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

    private String convertOutputValue(DataType outputValue) {
        return outputValue.toString();
    }

    private String convertOutputName(DataType outputName) {
        if (outputName instanceof Text) {
            return outputName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for expect outputName",
                    outputName.getClass()), Level.WARN);
            return outputName.toString();
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

    public ActionParameterOperation getOutputName() {
        return outputName;
    }

    public void setOutputName(ActionParameterOperation outputName) {
        this.outputName = outputName;
    }

    public ActionParameterOperation getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(ActionParameterOperation outputValue) {
        this.outputValue = outputValue;
    }

}