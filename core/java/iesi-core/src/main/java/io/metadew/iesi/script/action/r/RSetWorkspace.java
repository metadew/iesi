package io.metadew.iesi.script.action.r;

import io.metadew.iesi.connection.r.RWorkspace;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;

public class RSetWorkspace {

    private static  final String pathKey = "path";
    private static final String referenceNameKey = "name";
    private final ExecutionControl executionControl;
    private final ActionExecution actionExecution;
    private final HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private String referenceName;
    private String path;

    public RSetWorkspace(ExecutionControl executionControl,
                              ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
    }

    public void prepare() {
        ActionParameterOperation pathActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), pathKey);
        ActionParameterOperation referenceNameActionParameterOperation = new ActionParameterOperation(executionControl, actionExecution, actionExecution.getAction().getType(), referenceNameKey);

        // Get Parameters
        for (ActionParameter actionParameter : actionExecution.getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase(referenceNameKey)) {
                referenceNameActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase(pathKey)) {
                pathActionParameterOperation.setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        actionParameterOperationMap.put(referenceNameKey, referenceNameActionParameterOperation);
        actionParameterOperationMap.put(pathKey, pathActionParameterOperation);

        this.referenceName = convertReferenceName(referenceNameActionParameterOperation.getValue());
        this.path = convertPath(pathActionParameterOperation.getValue());
    }

    private String convertReferenceName(DataType referenceName) {
        if (referenceName == null) {
            throw new RuntimeException("No reference name defined for RSetWorkspace");
        } else if (referenceName instanceof Text) {
            return ((Text) referenceName).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("Reference name cannot be of type {0}", referenceName.getClass().getSimpleName()));
        }
    }

    private String convertPath(DataType path) {
        if (path == null) {
            throw new RuntimeException("No path defined for RSetWorkspace");
        } else if (path instanceof Text) {
            return ((Text) path).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("path cannot be of type {0}", path.getClass().getSimpleName()));
        }
    }

    public boolean execute() {
        try {
            RWorkspace rWorkspace = new RWorkspace(path);
            executionControl.getExecutionRuntime().setRWorkspace(referenceName, rWorkspace);
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
