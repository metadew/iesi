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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

public class RExecuteScript extends ActionTypeExecution {

    private static  final String scriptKey = "script";
    private static final String workspaceReferenceNameKey = "workspace";
    private String workspaceReferenceName;
    private String script;

    public RExecuteScript(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        ActionParameterOperation scriptActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), scriptKey);
        ActionParameterOperation workspaceReferenceNameActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), workspaceReferenceNameKey);

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(workspaceReferenceNameKey)) {
                workspaceReferenceNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(scriptKey)) {
                scriptActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put(workspaceReferenceNameKey, workspaceReferenceNameActionParameterOperation);
        getActionParameterOperationMap().put(scriptKey, scriptActionParameterOperation);

        this.workspaceReferenceName = convertWorkspaceReferenceName(workspaceReferenceNameActionParameterOperation.getValue());
        this.script = convertScript(scriptActionParameterOperation.getValue());
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

    private String convertScript(DataType path) {
        if (path == null) {
            throw new RuntimeException("No script defined for RPrepareWorkspace");
        } else if (path instanceof Text) {
            return ((Text) path).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("script cannot be of type {0}", path.getClass().getSimpleName()));
        }
    }


    @Override
    protected boolean executeAction() throws Exception {
        RWorkspace rWorkspace = getExecutionControl().getExecutionRuntime().getRWorkspace(workspaceReferenceName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Cannot find R workspace with name {0}", workspaceReferenceName)));
        RCommandResult rCommandResult = rWorkspace.executeScript(script);
        if (rCommandResult.getStatusCode().map(integer -> integer==0).orElse(false)) {
            getActionExecution().getActionControl().increaseSuccessCount();
            return true;
        } else {

            getActionExecution().getActionControl().increaseErrorCount();
            return false;
        }
    }

}
