package io.metadew.iesi.script.action.r;

import io.metadew.iesi.connection.r.RCommandResult;
import io.metadew.iesi.connection.r.RWorkspace;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;

import java.text.MessageFormat;

@Log4j2
public class RStartShinyApp extends ActionTypeExecution {

    private static final String portKey = "port";
    private static final String workspaceReferenceNameKey = "workspace";
    private String workspaceReferenceName;
    private int port;

    public RStartShinyApp(ExecutionControl executionControl,
                          ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        ActionParameterOperation scriptActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), portKey);
        ActionParameterOperation workspaceReferenceNameActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), workspaceReferenceNameKey);

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(workspaceReferenceNameKey)) {
                workspaceReferenceNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(portKey)) {
                scriptActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put(workspaceReferenceNameKey, workspaceReferenceNameActionParameterOperation);
        getActionParameterOperationMap().put(portKey, scriptActionParameterOperation);

        this.workspaceReferenceName = convertWorkspaceReferenceName(workspaceReferenceNameActionParameterOperation.getValue());
        this.port = convertPort(scriptActionParameterOperation.getValue());
    }

    private String convertWorkspaceReferenceName(DataType referenceName) {
        if (referenceName == null) {
            throw new RuntimeException("No workspace reference name defined for RPrepareWorkspace");
        } else if (referenceName instanceof Text) {
            return ((Text) referenceName).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("Workspace reference name cannot be of type {0}", referenceName.getClass().getSimpleName()));
        }
    }

    private int convertPort(DataType path) {
        if (path == null) {
            throw new RuntimeException("No script defined for RPrepareWorkspace");
        } else if (path instanceof Text) {
            return Integer.parseInt(((Text) path).getString());
        } else {
            throw new RuntimeException(MessageFormat.format("script cannot be of type {0}", path.getClass().getSimpleName()));
        }
    }

    @Override
    protected boolean executeAction() throws Exception {
        RWorkspace rWorkspace = getExecutionControl().getExecutionRuntime().getRWorkspace(workspaceReferenceName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find R workspace with name {0}", workspaceReferenceName)));
        RCommandResult rCommandResult = rWorkspace.executeCommand("shiny::runApp('" + FilenameUtils.separatorsToUnix(rWorkspace.getWorkspacePath().toString()) + "',port=" + port + ")", true);
        log.info("status:" + rCommandResult.getStatusCode());
        log.info("output:" + rCommandResult.getOutput());
        if (rCommandResult.getStatusCode().map(integer -> integer == 0).orElse(false)) {
            getActionExecution().getActionControl().increaseSuccessCount();
            return true;
        } else {
            getActionExecution().getActionControl().logOutput("action.error", "Could not start Shiny app, return code" + rCommandResult.getStatusCode().map(Object::toString).orElse("unknown"));
            getActionExecution().getActionControl().increaseErrorCount();
            return false;
        }
    }

}
