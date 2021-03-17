package io.metadew.iesi.script.action.java;

import io.metadew.iesi.connection.tools.HostConnectionTools;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.MessageFormat;

/**
 * Action type to parse java archive files.
 *
 * @author peter.billen
 */
public class JavaParseJar extends ActionTypeExecution {

    private static final String FILE_PATH_KEY = "path";
    private static final String FILE_NAME_KEY = "file";
    private static final String CONNECTION_NAME_KEY = "connection";
    private static final Logger LOGGER = LogManager.getLogger();

    public JavaParseJar(ExecutionControl executionControl,
                        ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() { }

    protected boolean executeAction() throws InterruptedException {

        String path = convertPath(getParameterResolvedValue(FILE_PATH_KEY));
        String fileName = convertFile(getParameterResolvedValue(FILE_NAME_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_NAME_KEY));
        boolean isOnLocalhost = HostConnectionTools.isOnLocalhost(
                connectionName, this.getExecutionControl().getEnvName());

        if (isOnLocalhost) {
            String filePath = fileName;
            if (!path.isEmpty()) filePath = path + File.separator + fileName;

            // JarOperation jarOperation = new JarOperation();
            // jarOperation.getJavaArchiveDefinition(filePath);
        } else {

        }

        return true;
    }

    @Override
    protected String getKeyword() {
        return "java.parseJava";
    }


    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connectionName",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertFile(DataType folderName) {
        if (folderName instanceof Text) {
            return folderName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderName",
                    folderName.getClass()));
            return folderName.toString();
        }
    }

    private String convertPath(DataType folderName) {
        if (folderName instanceof Text) {
            return folderName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for folderName",
                    folderName.getClass()));
            return folderName.toString();
        }
    }

    @SuppressWarnings("unused")
    private void setScope(String input) {
        this.getActionExecution().getActionControl().logOutput("java.parse", input);
    }

    @SuppressWarnings("unused")
    private void setError(String input) {
        this.getActionExecution().getActionControl().logOutput("java.parse.error", input);
        this.getActionExecution().getActionControl().increaseErrorCount();
    }

    @SuppressWarnings("unused")
    private void setSuccess() {
        this.getActionExecution().getActionControl().logOutput("java.parse.success", "confirmed");
        this.getActionExecution().getActionControl().increaseSuccessCount();
    }
}