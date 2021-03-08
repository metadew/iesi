package io.metadew.iesi.script.action.mod;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

public class ModSoapui extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation project;
    private ActionParameterOperation testSuite;
    private ActionParameterOperation testCase;
    private static final Logger LOGGER = LogManager.getLogger();


    public ModSoapui(ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Set Parameters
        this.setProject(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "project"));
        this.setTestSuite(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "suite"));
        this.setTestCase(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "case"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("project")) {
                this.getProject().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("suite")) {
                this.getTestSuite().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("case")) {
                this.getTestCase().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("project", this.getProject());
        this.getActionParameterOperationMap().put("suite", this.getTestSuite());
        this.getActionParameterOperationMap().put("case", this.getTestCase());
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

    protected boolean executeAction() throws InterruptedException {
        String project = convertProject(getProject().getValue());
        String testSuite = convertTestSuite(getTestSuite().getValue());
        String testCase = convertTestCase(getTestCase().getValue());
        // Output dir
        String output = this.getActionExecution().getActionControl().getActionRuntime().getRunCacheFolderName() + "soapui";
        FolderTools.createFolder(output);

        String command = FrameworkConfiguration.getInstance()
                .getMandatoryFrameworkFolder("modules")
                .getAbsolutePath()
                .resolve("soapui")
                .resolve("bin")
                .resolve("iesi-soapui.cmd")
                .toString();
        command = command + " -project " + project;
        if (!testSuite.isEmpty()) command = command + " -suite " + testSuite;
        if (!testCase.isEmpty()) command = command + " -case " + testCase;
        if (!output.isEmpty()) command = command + " -output " + output;


        HostConnection hostConnection = new HostConnection();
        ShellCommandResult shellCommandResult = hostConnection
                .executeLocalCommand("",
                        command
                );
        if (shellCommandResult.getReturnCode() == 0) {
            this.getActionExecution().getActionControl().increaseSuccessCount();
        } else {
            getActionExecution().getActionControl().logOutput("action.error", "'" + command + "' resulted with return code " + shellCommandResult.getReturnCode());
            this.getActionExecution().getActionControl().increaseErrorCount();
        }

        this.getActionExecution().getActionControl().logOutput("rc",
                Integer.toString(shellCommandResult.getReturnCode()));
        this.getActionExecution().getActionControl().logOutput("sys.out", shellCommandResult.getSystemOutput());
        this.getActionExecution().getActionControl().logOutput("err.out", shellCommandResult.getErrorOutput());

        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "mod.soapUi";
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
