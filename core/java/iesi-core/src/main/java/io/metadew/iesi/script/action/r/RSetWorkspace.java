package io.metadew.iesi.script.action.r;

import io.metadew.iesi.connection.r.RWorkspace;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;

import java.text.MessageFormat;

public class RSetWorkspace extends ActionTypeExecution {

    private static final String PATH_KEY = "path";
    private static final String REFERENCE_NAME_KEY = "name";
    private String referenceName;
    private String path;

    public RSetWorkspace(ExecutionControl executionControl,
                         ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
        this.referenceName = convertReferenceName(getParameterResolvedValue(REFERENCE_NAME_KEY));
        this.path = convertPath(getParameterResolvedValue(PATH_KEY));
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
