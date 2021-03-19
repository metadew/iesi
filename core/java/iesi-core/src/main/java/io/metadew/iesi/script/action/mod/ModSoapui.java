package io.metadew.iesi.script.action.mod;

import io.metadew.iesi.common.configuration.framework.FrameworkConfiguration;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.host.ShellCommandResult;
import io.metadew.iesi.connection.tools.FolderTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;

@Log4j2
public class ModSoapui extends ActionTypeExecution {

    // Parameters
    private static final String PROJECT_KEY = "project";
    private static final String SUITE_KEY = "suite";
    private static final String CASE_KEY = "case";


    public ModSoapui(ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
    }

    private String convertProject(DataType project) {
        if (project instanceof Text) {
            return project.toString();
        } else {
            log.warn(MessageFormat.format(
                    this.getActionExecution().getAction().getType() + " does not accept {0} as type for project",
                    project.getClass()));
            return project.toString();
        }
    }

    private String convertTestSuite(DataType testSuite) {
        if (testSuite instanceof Text) {
            return testSuite.toString();
        } else {
            log.warn(MessageFormat.format(
                    this.getActionExecution().getAction().getType() + " does not accept {0} as type for suite",
                    testSuite.getClass()));
            return testSuite.toString();
        }
    }

    private String convertTestCase(DataType testCase) {
        if (testCase instanceof Text) {
            return testCase.toString();
        } else {
            log.warn(MessageFormat.format(
                    this.getActionExecution().getAction().getType() + " does not accept {0} as type for case",
                    testCase.getClass()));
            return testCase.toString();
        }
    }

    protected boolean executeAction() throws InterruptedException {
        String project = convertProject(getParameterResolvedValue(PROJECT_KEY));
        String testSuite = convertTestSuite(getParameterResolvedValue(SUITE_KEY));
        String testCase = convertTestCase(getParameterResolvedValue(CASE_KEY));
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


}
