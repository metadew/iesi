package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This action prints a message for logging of debugging purposes
 *
 * @author Peter Billen
 */
public class FwkOutputMessage extends ActionTypeExecution {

    private ActionParameterOperation message;
    private ActionParameterOperation onScreen;
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkOutputMessage(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setMessage(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "message"));
        this.setOnScreen(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "onScreen"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("message")) {
                this.getMessage().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("onscreen")) {
                this.getOnScreen().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("message", this.getMessage());
        this.getActionParameterOperationMap().put("onScreen", this.getOnScreen());
    }

    private boolean convertOnScreen(DataType onScreen) {
        if (onScreen instanceof Text) {
            return onScreen.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for onScreen",
                    onScreen.getClass()));
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
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for message",
                    messages.getClass()));
            messageList.add(messages.toString());
        }
        return messageList;
    }

    protected boolean executeAction() throws InterruptedException {
        List<String> messages = convertMessages(getMessage().getValue());
        boolean onScreen = convertOnScreen(getOnScreen().getValue());
        final Level level = onScreen ? Level.INFO : Level.DEBUG;

        messages.forEach(message -> {
            if (message.trim().isEmpty()) {
                GenerationObjectExecution generationObjectExecution = new GenerationObjectExecution();
                this.getExecutionControl().logMessage(
                        "action.message=" + generationObjectExecution.getMotd().message(), level);
            } else {
                this.getExecutionControl().logMessage(
                        "action.message=" + message, level);
            }
            this.getActionExecution().getActionControl().increaseSuccessCount();
        });
        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.outputMessage";
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