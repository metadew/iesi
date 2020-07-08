package io.metadew.iesi.script.action.script;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.script.result.ScriptResultOutput;
import io.metadew.iesi.metadata.definition.script.result.key.ScriptResultOutputKey;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;


@Log4j2
public class ScriptLogOutput {

    private final ScriptExecution scriptExecution;
    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation outputName;
    private ActionParameterOperation outputValue;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private static final Logger LOGGER = LogManager.getLogger();

    public ScriptLogOutput(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.scriptExecution = scriptExecution;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare() {
        // Reset Parameters
        this.outputName = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), "name");
        this.outputValue = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), "value");

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("name")) {
                outputName.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("value")) {
                outputValue.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        actionParameterOperationMap.put("name", outputName);
        actionParameterOperationMap.put("value", outputValue);
    }

    public boolean execute() throws InterruptedException {
        try {
            return executeOperation();
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean executeOperation() throws InterruptedException {
        String name = convertOutputName(outputName.getValue());
        String value = convertOutputValue(outputValue.getValue());

        // Log the output in the action as well
        actionExecution.getActionControl().logOutput("output.name", name);
        actionExecution.getActionControl().logOutput("output.value", value);

        // log the output in the script
        value = FrameworkCrypto.getInstance().redact(value);
        log.info("action.output=" + name + ":" + value, Level.INFO);
        ScriptResultOutput scriptResultOutput = new ScriptResultOutput(
                new ScriptResultOutputKey(executionControl.getRunId(), executionControl.getProcessId(), name),
                scriptExecution.getScript().getMetadataKey().getScriptId(),
                value);
        ScriptResultOutputConfiguration.getInstance().insert(scriptResultOutput);

        actionExecution.getActionControl().increaseSuccessCount();
        return true;
    }

    private String convertOutputValue(DataType outputValue) {
        return outputValue.toString();
    }

    private String convertOutputName(DataType outputName) {
        if (outputName instanceof Text) {
            return outputName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(actionExecution.getAction().getType() + " does not accept {0} as type for expect outputName",
                    outputName.getClass()));
            return outputName.toString();
        }
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

}