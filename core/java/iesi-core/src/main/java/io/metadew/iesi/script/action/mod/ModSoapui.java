package io.metadew.iesi.script.action.mod;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;

public class ModSoapui {

	private ActionExecution actionExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation project;
	private ActionParameterOperation testSuite;
	private ActionParameterOperation testCase;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
	private static final Logger LOGGER = LogManager.getLogger();

	// Constructors
	public ModSoapui() {

	}

	public ModSoapui(ExecutionControl executionControl,
			ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.init(executionControl, scriptExecution, actionExecution);
	}

	public void init(ExecutionControl executionControl,
			ScriptExecution scriptExecution, ActionExecution actionExecution) {
		this.setExecutionControl(executionControl);
		this.setActionExecution(actionExecution);
		this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
	}

	public void prepare()  {
		// Set Parameters
		this.setProject(new ActionParameterOperation(this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "project"));
		this.setTestSuite(new ActionParameterOperation(this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "suite"));
		this.setTestCase(new ActionParameterOperation(this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "case"));

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("project")) {
				this.getProject().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
			} else if (actionParameter.getName().equalsIgnoreCase("suite")) {
				this.getTestSuite().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
			} else if (actionParameter.getName().equalsIgnoreCase("case")) {
				this.getTestCase().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
			}
		}

		// Create parameter list
		this.getActionParameterOperationMap().put("project", this.getProject());
		this.getActionParameterOperationMap().put("suite", this.getTestSuite());
		this.getActionParameterOperationMap().put("case", this.getTestCase());
	}

	public boolean execute() {
		try {
			String project = convertProject(getProject().getValue());
			String testSuite = convertTestSuite(getTestSuite().getValue());
			String testCase = convertTestCase(getTestCase().getValue());
			return execute(project, testSuite, testCase);
		} catch (Exception e) {
			StringWriter StackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(StackTrace));

			this.getActionExecution().getActionControl().increaseErrorCount();

			this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
			this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

			return false;
		}

	}

	private String convertProject(DataType project) {
		if (project instanceof Text) {
			return project.toString();
		} else {
			LOGGER.warn(MessageFormat.format(
					this.getActionExecution().getAction().getType() + " does not accept {0} as type for project",
					project.getClass()));
			return project.toString();
		}
	}

	private String convertTestSuite(DataType testSuite) {
		if (testSuite instanceof Text) {
			return testSuite.toString();
		} else {
			LOGGER.warn(MessageFormat.format(
							this.getActionExecution().getAction().getType() + " does not accept {0} as type for suite",
							testSuite.getClass()));
			return testSuite.toString();
		}
	}

	private String convertTestCase(DataType testCase) {
		if (testCase instanceof Text) {
			return testCase.toString();
		} else {
			LOGGER.warn(MessageFormat.format(
							this.getActionExecution().getAction().getType() + " does not accept {0} as type for case",
							testCase.getClass()));
			return testCase.toString();
		}
	}

	private boolean execute(String project, String testSuite, String testCase) {
		// Output dir
		String output = this.getActionExecution().getActionControl().getActionRuntime().getRunCacheFolderName() + "soapui";
		FolderTools.createFolder(output);
				
		String command = FrameworkFolderConfiguration.getInstance().getFolderAbsolutePath("modules") +
				File.separator  + "soapui" + File.separator + "bin" + File.separator + "iesi-soapui.cmd";
		command = command + " -project " + project;
		if (!testSuite.isEmpty()) command = command + " -suite " + testSuite;
		if (!testCase.isEmpty()) command = command + " -case " + testCase;
		if (!output.isEmpty()) command = command + " -output " + output;

		
		
		HostConnection hostConnection = new HostConnection();
		ShellCommandResult shellCommandResult = hostConnection
				.executeLocalCommand("",
						command,
						null);
		if (shellCommandResult.getReturnCode() == 0) {
			this.getActionExecution().getActionControl().increaseSuccessCount();
		} else {
			this.getActionExecution().getActionControl().increaseErrorCount();
		}

		this.getActionExecution().getActionControl().logOutput("rc",
				Integer.toString(shellCommandResult.getReturnCode()));
		this.getActionExecution().getActionControl().logOutput("sys.out", shellCommandResult.getSystemOutput());
		this.getActionExecution().getActionControl().logOutput("err.out", shellCommandResult.getErrorOutput());

		this.getActionExecution().getActionControl().increaseSuccessCount();
		return true;
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

	public ActionParameterOperation getActionParameterOperation(String key) {
		return this.getActionParameterOperationMap().get(key);
	}

	public ActionParameterOperation getProject() {
		return project;
	}

	public void setProject(ActionParameterOperation project) {
		this.project = project;
	}

	public ActionParameterOperation getTestSuite() {
		return testSuite;
	}

	public void setTestSuite(ActionParameterOperation testSuite) {
		this.testSuite = testSuite;
	}

	public ActionParameterOperation getTestCase() {
		return testCase;
	}

	public void setTestCase(ActionParameterOperation testCase) {
		this.testCase = testCase;
	}

}
