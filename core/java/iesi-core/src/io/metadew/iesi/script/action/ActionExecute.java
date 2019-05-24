package io.metadew.iesi.script.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

import org.apache.logging.log4j.Level;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

/**
 * This action executes another action from within the script
 * 
 * @author Peter Billen
 *
 */
public class ActionExecute {

	private ActionExecution actionExecution;
	private ScriptExecution scriptExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation name;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public ActionExecute() {

	}

	public ActionExecute(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
			ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
	}

	public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
			ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.setFrameworkExecution(frameworkExecution);
		this.setExecutionControl(executionControl);
		this.setScriptExecution(scriptExecution);
		this.setActionExecution(actionExecution);
		this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
	}

	public void prepare() {
		// Reset Parameters
		this.setName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("name")) {
				this.getName().setInputValue(actionParameter.getValue());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("name", this.getName());
	}
	
    public boolean execute() {
        try {
            String actionName = convertActionName(getName().getValue());
            return executeAction(actionName);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    private String convertActionName(DataType actionName) {
        if (actionName instanceof Text) {
            return actionName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
            		actionName.getClass()), Level.WARN);
            return actionName.toString();
        }
    }

	public boolean executeAction(String actionName) {
		try {
			this.getExecutionControl().logMessage(this.getActionExecution(), "action.execute.start", Level.INFO);

			// Get the action
			Action action = null;
			boolean result = false;
			for (int i = 0; i < this.getScriptExecution().getActions().size(); i++) {
				action = this.getScriptExecution().getActions().get(i);
				if (action.getName().equalsIgnoreCase(actionName)) {
					result = true;
					break;
				}
			}
			if (!result) {
				throw new RuntimeException("action.name.notfound");
			}
			
			ActionExecution actionExecution = new ActionExecution(this.getFrameworkExecution(),
					this.getExecutionControl(), this.getScriptExecution(), action);
			
			// Initialize
			actionExecution.initialize();
			actionExecution.execute(null);
			// TODO make use of script execution in order to leverage iterations as well
			// In script execution --> create operation for logic
			// Verify thread safe operations

			this.getExecutionControl().logMessage(this.getActionExecution(), "action.execute.end", Level.INFO);
			
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

	public ActionParameterOperation getName() {
		return name;
	}

	public void setName(ActionParameterOperation name) {
		this.name = name;
	}

	public ScriptExecution getScriptExecution() {
		return scriptExecution;
	}

	public void setScriptExecution(ScriptExecution scriptExecution) {
		this.scriptExecution = scriptExecution;
	}

}