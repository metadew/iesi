package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import org.apache.logging.log4j.Level;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

/**
 * This action prints a message for logging of debugging purposes
 * 
 * @author Peter Billen
 *
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
		this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
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
			// Verify if the message is empty
			if (this.getMessage().getValue().trim().isEmpty()) {
				GenerationObjectExecution generationObjectExecution = new GenerationObjectExecution(this.getFrameworkExecution());
				this.getMessage().setInputValue(generationObjectExecution.getMotd().message());
			}
			
			// Verify if onScreen is empty
			if (this.getOnScreen().getValue().trim().isEmpty()) {
				this.getOnScreen().setInputValue("N");
			}
			
			// Verify if the message needs to appear on the screen
			Level level = Level.DEBUG;
			if (this.getOnScreen().getValue().equalsIgnoreCase("y")) {
				level = Level.INFO;
			}
			this.getExecutionControl().logMessage(this.getActionExecution(),
					"action.message=" + this.getMessage().getValue(), level);

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