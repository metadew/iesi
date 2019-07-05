package io.metadew.iesi.script.action;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.datatypes.Array;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This action prints a message for logging of debugging purposes
 *
 * @author Peter Billen
 */
public class FwkOutputMessage {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation message;
    private ActionParameterOperation onScreen;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public FwkOutputMessage() {

    }

    public FwkOutputMessage(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<>());
    }

    public void prepare() {
        // Reset Parameters
        this.setMessage(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "message"));
        this.setOnScreen(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "onScreen"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("message")) {
                this.getMessage().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("onscreen")) {
                this.getOnScreen().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("message", this.getMessage());
        this.getActionParameterOperationMap().put("onScreen", this.getOnScreen());
    }


    public boolean execute() {
        try {
            List<String> messages = convertMessages(getMessage().getValue());
            boolean onScreen = convertOnScreen(getOnScreen().getValue());
            return outputMessage(messages, onScreen);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    private boolean convertOnScreen(DataType onScreen) {
        if (onScreen instanceof Text) {
            return onScreen.toString().equalsIgnoreCase("y");
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for onScreen",
                    onScreen.getClass()), Level.WARN);
            return false;
        }
    }

    private List<String> convertMessages(DataType messages) {
        ArrayList<String> messageList = new ArrayList<>();
        if (messages instanceof Text) {
            messageList.add(messages.toString());
        } else if (messages instanceof Array) {
            for (DataType listElement : ((Array) messages).getList()) {
                messageList.addAll(convertMessages(listElement));
            }
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for message",
                    messages.getClass()), Level.WARN);
            messageList.add(messages.toString());
        }
        return messageList;
    }

    private boolean outputMessage(List<String> messages, boolean onScreen) {
        final Level level = onScreen ? Level.INFO : Level.DEBUG;

        messages.forEach(message -> {
            if (message.trim().isEmpty()) {
                GenerationObjectExecution generationObjectExecution = new GenerationObjectExecution(this.getFrameworkExecution());
                this.getExecutionControl().logMessage(this.getActionExecution(),
                        "action.message=" + generationObjectExecution.getMotd().message(), level);
            } else {
                this.getExecutionControl().logMessage(this.getActionExecution(),
                        "action.message=" + message, level);
            }
            this.getActionExecution().getActionControl().increaseSuccessCount();
        });
        return true;
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

    public ActionParameterOperation getMessage() {
        return message;
    }

    public void setMessage(ActionParameterOperation message) {
        this.message = message;
    }

    public ActionParameterOperation getOnScreen() {
        return onScreen;
    }

    public void setOnScreen(ActionParameterOperation onScreen) {
        this.onScreen = onScreen;
    }

}