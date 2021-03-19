package io.metadew.iesi.script.action.r;

import io.metadew.iesi.connection.r.RCommandResult;
import io.metadew.iesi.connection.r.RWorkspace;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;

import java.text.MessageFormat;

public class RExecuteScript extends ActionTypeExecution {

    private static final String SCRIPT_KEY = "script";
    private static final String WORKSPACE_REFERENCE_NAME_KEY = "workspace";
    private String workspaceReferenceName;
    private String script;

    public RExecuteScript(ExecutionControl executionControl,
                             ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        this.workspaceReferenceName = convertWorkspaceReferenceName(getParameterResolvedValue(WORKSPACE_REFERENCE_NAME_KEY));
        this.script = convertScript(getParameterResolvedValue(SCRIPT_KEY));
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
            getActionExecution().getActionControl().logOutput("action.error", "R script '" + script + "' resulted with return code " + rCommandResult.getStatusCode().map(Object::toString).orElse("unknown"));
            getActionExecution().getActionControl().increaseErrorCount();
            return false;
        }
    }

    @Override
    protected String getKeyword() {
        return "r.executeScript";
    }

}
