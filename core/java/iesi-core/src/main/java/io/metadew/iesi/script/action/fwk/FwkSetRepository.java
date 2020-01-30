package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class FwkSetRepository {

    private ActionExecution actionExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation repositoryReferenceName;
    private ActionParameterOperation repositoryName;
    private ActionParameterOperation repositoryInstanceName;
    private ActionParameterOperation repositoryInstanceLabels;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;
    private DataTypeService dataTypeService;
    private static final Logger LOGGER = LogManager.getLogger();

    // Constructors
    public FwkSetRepository() {

    }

    public FwkSetRepository(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.init(executionControl, scriptExecution, actionExecution);
    }

    public void init(ExecutionControl executionControl,
                     ScriptExecution scriptExecution, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.actionParameterOperationMap = new HashMap<>();
        this.dataTypeService = new DataTypeService();
    }

    public void prepare()  {
        // Reset Parameters
        this.setRepositoryReferenceName(
                new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
        this.setRepositoryName(new ActionParameterOperation(this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "repository"));
        this.setRepositoryInstanceName(
                new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "instance"));
        this.setRepositoryInstanceLabels(
                new ActionParameterOperation(this.getExecutionControl(),
                        this.getActionExecution(), this.getActionExecution().getAction().getType(), "labels"));
        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("repository")) {
                this.getRepositoryName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("name")) {
                this.getRepositoryReferenceName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("instance")) {
                this.getRepositoryInstanceName().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            } else if (actionParameter.getName().equalsIgnoreCase("labels")) {
                this.getRepositoryInstanceLabels().setInputValue(actionParameter.getValue(), executionControl.getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("name", this.getRepositoryReferenceName());
        this.getActionParameterOperationMap().put("repository", this.getRepositoryName());
        this.getActionParameterOperationMap().put("instance", this.getRepositoryInstanceName());
        this.getActionParameterOperationMap().put("labels", this.getRepositoryInstanceLabels());
    }

    //
    public boolean execute() throws InterruptedException {
        try {
            return executionOperation();
        } catch (InterruptedException e) {
            throw (e);
        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }
    }

    private boolean executionOperation() throws SQLException, InterruptedException {
        String repositoryReferenceName = convertRepositoryReferenceName(getRepositoryReferenceName().getValue());
        String repositoryName = convertRepositoryName(getRepositoryName().getValue());
        String repositoryInstanceName = convertRepositoryInstanceName(getRepositoryInstanceName().getValue());
        List<String> repositoryInstanceLabels = convertRepositoryInstanceLabels(getRepositoryInstanceLabels().getValue());

        // Run the action
        this.getExecutionControl().getExecutionRuntime().setRepository(this.getExecutionControl(), repositoryReferenceName,
                repositoryName, repositoryInstanceName, String.join(",", repositoryInstanceLabels));

        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
    }

    private String convertRepositoryInstanceName(DataType repositoryInstanceName) {
        if (repositoryInstanceName instanceof Text) {
            return repositoryInstanceName.toString();
        } else {
        	LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for repository instance name",
                    repositoryInstanceName.getClass()));
            return repositoryInstanceName.toString();
        }
    }

    private String convertRepositoryReferenceName(DataType referenceName) {
        if (referenceName instanceof Text) {
            return referenceName.toString();
        } else {
        	LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for reference name",
                    referenceName.getClass()));
            return referenceName.toString();
        }
    }


    private List<String> convertRepositoryInstanceLabels(DataType repositoryLabels) {
        List<String> labels = new ArrayList<>();
        if (repositoryLabels instanceof Text) {
            Arrays.stream(repositoryLabels.toString().split(","))
                    .forEach(repositoryLabel -> labels.add(convertRepositoryInstanceLabel(dataTypeService.resolve(repositoryLabel.trim(), executionControl.getExecutionRuntime()))));
            return labels;
        } else if (repositoryLabels instanceof Array) {
            ((Array) repositoryLabels).getList()
                    .forEach(datasetLabel -> labels.add(convertRepositoryInstanceLabel(datasetLabel)));
            return labels;
        } else {
            // TODO: log with framework
            System.out.println(MessageFormat.format("fwk.setRepository does not accept {0} as type for repository instance labels",
                    repositoryLabels.getClass()));
            return labels;
        }
    }

    private String convertRepositoryName(DataType repositoryName) {
        if (repositoryName instanceof Text) {
            return repositoryName.toString();
        } else {
            // TODO: log
            System.out.println(MessageFormat.format("fwk.stRepository does not accept {0} as type for repository name",
                    repositoryName.getClass()));
            return repositoryName.toString();
        }
    }

    private String convertRepositoryInstanceLabel(DataType datasetLabel) {
        if (datasetLabel instanceof Text) {
            return datasetLabel.toString();
        } else {
            // TODO: log
            System.out.println(MessageFormat.format("Repository instance does not accept {0} as type for a repository label",
                    datasetLabel.getClass()));
            return datasetLabel.toString();
        }
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

    public ActionParameterOperation getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(ActionParameterOperation repositoryName) {
        this.repositoryName = repositoryName;
    }

    public ActionParameterOperation getRepositoryInstanceName() {
        return repositoryInstanceName;
    }

    public void setRepositoryInstanceName(ActionParameterOperation repositoryInstanceName) {
        this.repositoryInstanceName = repositoryInstanceName;
    }

    public ActionParameterOperation getRepositoryInstanceLabels() {
        return repositoryInstanceLabels;
    }

    public void setRepositoryInstanceLabels(ActionParameterOperation repositoryInstanceLabels) {
        this.repositoryInstanceLabels = repositoryInstanceLabels;
    }

    public ActionParameterOperation getRepositoryReferenceName() {
        return repositoryReferenceName;
    }

    public void setRepositoryReferenceName(ActionParameterOperation repositoryReferenceName) {
        this.repositoryReferenceName = repositoryReferenceName;
    }

}