package io.metadew.iesi.script.action.java;

import io.metadew.iesi.connection.tools.HostConnectionTools;
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

import java.io.File;
import java.text.MessageFormat;

/**
 * Action type to parse java archive files.
 *
 * @author peter.billen
 */
public class JavaParseJar extends ActionTypeExecution {

    // Parameters
    private ActionParameterOperation filePath;
    private ActionParameterOperation fileName;
    private ActionParameterOperation connectionName;
    private static final Logger LOGGER = LogManager.getLogger();

    public JavaParseJar(ExecutionControl executionControl,
                        ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        // Reset Parameters
        this.setFilePath(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "path"));
        this.setFileName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "file"));
        this.setConnectionName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "connection"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("path")) {
                this.getFilePath().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("file")) {
                this.getFileName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("connection")) {
                this.getConnectionName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("path", this.getFilePath());
        this.getActionParameterOperationMap().put("file", this.getFileName());
        this.getActionParameterOperationMap().put("connection", this.getConnectionName());
    }


    protected boolean executeAction() throws InterruptedException {

        String path = convertPath(getFilePath().getValue());
        String fileName = convertFile(getFileName().getValue());
        String connectionName = convertConnectionName(getConnectionName().getValue());
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

    public ActionParameterOperation getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(ActionParameterOperation connectionName) {
        this.connectionName = connectionName;
    }

    public ActionParameterOperation getFilePath() {
        return filePath;
    }

    public void setFilePath(ActionParameterOperation filePath) {
        this.filePath = filePath;
    }

    public ActionParameterOperation getFileName() {
        return fileName;
    }

    public void setFileName(ActionParameterOperation fileName) {
        this.fileName = fileName;
    }

}