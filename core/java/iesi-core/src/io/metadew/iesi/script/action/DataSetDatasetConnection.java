package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.Array;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeResolver;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Level;


public class DataSetDatasetConnection {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation referenceName;
    private ActionParameterOperation datasetType;
    private ActionParameterOperation datasetName;
    private ActionParameterOperation datasetLabels;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public DataSetDatasetConnection() {

    }

    public DataSetDatasetConnection(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                    ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(frameworkExecution, executionControl, scriptExecution, actionExecution);
    }

    public void init(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setActionParameterOperationMap(new HashMap<String, ActionParameterOperation>());
    }

    public void prepare() {
        // Reset Parameters
        this.setReferenceName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
        this.setDatasetType(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "type"));
        this.setDatasetName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "dataset"));
        this.setDatasetLabels(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "labels"));
        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("name")) {
                this.getReferenceName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("type")) {
                this.getDatasetType().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("dataset")) {
                this.getDatasetName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("labels")) {
                this.getDatasetLabels().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("name", this.getReferenceName());
        this.getActionParameterOperationMap().put("type", this.getDatasetType());
        this.getActionParameterOperationMap().put("dataset", this.getDatasetName());
        this.getActionParameterOperationMap().put("labels", this.getDatasetLabels());
    }

    //
    @SuppressWarnings("unused")
	public boolean execute() {
        try {
            String referenceName = convertDatasetReferenceName(getReferenceName().getValue());
            String datasetName = convertDatasetName(getDatasetName().getValue());
            String datasetType = convertDatasetType(getDatasetType().getValue());
            List<String> labels = convertDatasetLabels(getDatasetLabels().getValue());

            return setDatasetConnection(referenceName, datasetName, labels);

        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean setDatasetConnection(String referenceName, String datasetName, List<String> labels) throws IOException, SQLException {
        // TODO: use dataset data type
        this.getExecutionControl().getExecutionRuntime().setKeyValueDataset(referenceName, datasetName, labels);
        return true;
    }

    private String convertDatasetReferenceName(DataType referenceName) {
        if (referenceName instanceof Text) {
            return referenceName.toString();
        } else {
        	this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +   " does not accept {0} as type for reference name",
                    referenceName.getClass()),Level.WARN);
            return referenceName.toString();
        }
    }


    private List<String> convertDatasetLabels(DataType datasetLabels) {
        List<String> labels = new ArrayList<>();
        if (datasetLabels instanceof Text) {
            Arrays.stream(datasetLabels.toString().split(","))
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(DataTypeResolver.resolveToDataType(datasetLabel.trim(), frameworkExecution.getFrameworkConfiguration().getFolderConfiguration(), executionControl.getExecutionRuntime()))));
            return labels;
        } else if (datasetLabels instanceof Array) {
            ((Array) datasetLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertDatasetLabel(datasetLabel)));
            return labels;
        } else {
        	this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for dataset labels",
                    datasetLabels.getClass()),Level.WARN);
            return labels;
        }
    }

    private String convertDatasetType(DataType datasetType) {
        if (datasetType == null) {
            return "";
        }
        if (datasetType instanceof Text) {
            return datasetType.toString();
        } else {
        	this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for dataset type",
                    datasetType.getClass()),Level.WARN);
            return datasetType.toString();
        }
    }

    private String convertDatasetName(DataType datasetName) {
        if (datasetName instanceof Text) {
            return datasetName.toString();
        } else {
        	this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for dataset name",
                    datasetName.getClass()),Level.WARN);
            return datasetName.toString();
        }
    }

    private String convertDatasetLabel(DataType datasetLabel) {
        if (datasetLabel instanceof Text) {
            return datasetLabel.toString();
        } else {
        	this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() +  " does not accept {0} as type for a dataset label",
                    datasetLabel.getClass()), Level.WARN);
            return datasetLabel.toString();
        }
    }

    // Getters and Setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public HashMap<String, ActionParameterOperation> getActionParameterOperationMap() {
        return actionParameterOperationMap;
    }

    public void setActionParameterOperationMap(HashMap<String, ActionParameterOperation> actionParameterOperationMap) {
        this.actionParameterOperationMap = actionParameterOperationMap;
    }

    public ActionParameterOperation getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(ActionParameterOperation datasetName) {
        this.datasetName = datasetName;
    }

    public ActionParameterOperation getDatasetLabels() {
        return datasetLabels;
    }

    public void setDatasetLabels(ActionParameterOperation datasetLabels) {
        this.datasetLabels = datasetLabels;
    }

    public ActionParameterOperation getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(ActionParameterOperation referenceName) {
        this.referenceName = referenceName;
    }

    public ActionParameterOperation getDatasetType() {
        return datasetType;
    }

    public void setDatasetType(ActionParameterOperation datasetType) {
        this.datasetType = datasetType;
    }

}