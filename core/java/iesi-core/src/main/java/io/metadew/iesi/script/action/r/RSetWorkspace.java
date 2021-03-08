package io.metadew.iesi.script.action.r;

import io.metadew.iesi.connection.r.RWorkspace;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.text.MessageFormat;

public class RSetWorkspace extends ActionTypeExecution {

    private static final String pathKey = "path";
    private static final String referenceNameKey = "name";
    private String referenceName;
    private String path;

    public RSetWorkspace(ExecutionControl executionControl,
                         ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
        ActionParameterOperation pathActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), pathKey);
        ActionParameterOperation referenceNameActionParameterOperation = new ActionParameterOperation(getExecutionControl(), getActionExecution(), getActionExecution().getAction().getType(), referenceNameKey);

        // Get Parameters
        for (ActionParameter actionParameter : getActionExecution().getAction().getParameters()) {
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(referenceNameKey)) {
                referenceNameActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase(pathKey)) {
                pathActionParameterOperation.setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        getActionParameterOperationMap().put(referenceNameKey, referenceNameActionParameterOperation);
        getActionParameterOperationMap().put(pathKey, pathActionParameterOperation);

        this.referenceName = convertReferenceName(referenceNameActionParameterOperation.getValue());
        this.path = convertPath(pathActionParameterOperation.getValue());
    }

    @Override
    protected boolean executeAction() throws Exception {
        RWorkspace rWorkspace = new RWorkspace(path);
        getExecutionControl().getExecutionRuntime().setRWorkspace(referenceName, rWorkspace);
        return true;
    }

    @Override
    protected String getKeyword() {
        return "r.setWorkspace";
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

}
