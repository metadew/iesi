package io.metadew.iesi.script.action;

import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

public class ModSoapui {

	private ActionExecution actionExecution;
	private FrameworkExecution frameworkExecution;
	private ExecutionControl executionControl;

	// Parameters
	private ActionParameterOperation project;
	private ActionParameterOperation testSuite;
	private ActionParameterOperation testCase;
	private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

	// Constructors
	public ModSoapui() {

	}

	public ModSoapui(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
		// Set Parameters
		this.setProject(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "project"));
		this.setTestSuite(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "suite"));
		this.setTestCase(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
				this.getActionExecution(), this.getActionExecution().getAction().getType(), "case"));

		// Get Parameters
		for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
			if (actionParameter.getName().equalsIgnoreCase("project")) {
				this.getProject().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("suite")) {
				this.getTestSuite().setInputValue(actionParameter.getValue());
			} else if (actionParameter.getName().equalsIgnoreCase("case")) {
				this.getTestCase().setInputValue(actionParameter.getValue());
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
			this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(
					this.getActionExecution().getAction().getType() + " does not accept {0} as type for project",
					project.getClass()), Level.WARN);
			return project.toString();
		}
	}

	private String convertTestSuite(DataType testSuite) {
		if (testSuite instanceof Text) {
			return testSuite.toString();
		} else {
			this.getFrameworkExecution().getFrameworkLog()
					.log(MessageFormat.format(
							this.getActionExecution().getAction().getType() + " does not accept {0} as type for suite",
							testSuite.getClass()), Level.WARN);
			return testSuite.toString();
		}
	}

	private String convertTestCase(DataType testCase) {
		if (testCase instanceof Text) {
			return testCase.toString();
		} else {
			this.getFrameworkExecution().getFrameworkLog()
					.log(MessageFormat.format(
							this.getActionExecution().getAction().getType() + " does not accept {0} as type for case",
							testCase.getClass()), Level.WARN);
			return testCase.toString();
		}
	}

	private boolean execute(String project, String testSuite, String testCase) {
		// Output dir
		String output = this.getActionExecution().getActionControl().getActionRuntime().getRunCacheFolderName() + "soapui";
		FolderTools.createFolder(output);
				
		String command = this.getFrameworkExecution().getFrameworkConfiguration().getFolderConfiguration()
				.getFolderAbsolutePath("modules") + File.separator  + "soapui" + File.separator + "bin" + File.separator
				+ "iesi-soapui.cmd";
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
