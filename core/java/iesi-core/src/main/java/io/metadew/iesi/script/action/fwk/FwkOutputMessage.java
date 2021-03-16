package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
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

    private static final String MESSAGE_KEY = "message";
    private static final String ON_SCREEN_KEY = "onScreen";
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkOutputMessage(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() { }

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
        List<String> messages = convertMessages(getParameterResolvedValue(MESSAGE_KEY));
        boolean onScreen = convertOnScreen(getParameterResolvedValue(ON_SCREEN_KEY));
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

}