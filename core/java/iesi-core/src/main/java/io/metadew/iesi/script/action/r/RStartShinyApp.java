package io.metadew.iesi.script.action.r;

import io.metadew.iesi.connection.r.RCommandResult;
import io.metadew.iesi.connection.r.RWorkspace;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

public class RStartShinyApp {

    private static final Logger LOGGER = LogManager.getLogger();
    private static  final String portKey = "port";
    private static final String workspaceReferenceNameKey = "workspace";
    private final ExecutionControl executionControl;
    private final ActionExecution actionExecution;
    private final HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private String workspaceReferenceName;
    private int port;

    public RStartShinyApp(ExecutionControl executionControl,
                          ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare() throws Exception {
        ActionParameterOperation scriptActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), portKey);
        ActionParameterOperation workspaceReferenceNameActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), workspaceReferenceNameKey);

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(workspaceReferenceNameKey)) {
                workspaceReferenceNameActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(portKey)) {
                scriptActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        actionParameterOperationMap.put(workspaceReferenceNameKey, workspaceReferenceNameActionParameterOperation);
        actionParameterOperationMap.put(portKey, scriptActionParameterOperation);

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

    public boolean execute() {
        try {
            RWorkspace rWorkspace = executionControl.getExecutionRuntime().getRWorkspace(workspaceReferenceName)
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find R workspace with name {0}", workspaceReferenceName)));
            RCommandResult rCommandResult = rWorkspace.executeCommand("shiny::runApp('" + FilenameUtils.separatorsToUnix(rWorkspace.getWorkspacePath().toString()) +"',port=" + port + ")", true);
//            LOGGER.info("status:" + rCommandResult.getStatusCode());
//            LOGGER.info("output:" + rCommandResult.getOutput());
//            if (rCommandResult.getStatusCode().map(integer -> integer==0).orElse(false)) {
//                actionExecution.getActionControl().increaseSuccessCount();
//                return true;
//            } else {
//                actionExecution.getActionControl().increaseErrorCount();
//                return false;
//            }
            return true;
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            actionExecution.getActionControl().increaseErrorCount();

            actionExecution.getActionControl().logOutput("exception", e.getMessage());
            actionExecution.getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

}
