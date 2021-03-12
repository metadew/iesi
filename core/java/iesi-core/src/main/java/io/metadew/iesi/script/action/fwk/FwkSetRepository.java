package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FwkSetRepository extends ActionTypeExecution {

    private ActionParameterOperation repositoryReferenceName;
    private ActionParameterOperation repositoryName;
    private ActionParameterOperation repositoryInstanceName;
    private ActionParameterOperation repositoryInstanceLabels;
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkSetRepository(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepare() {
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
            if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("repository")) {
                this.getRepositoryName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("name")) {
                this.getRepositoryReferenceName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("instance")) {
                this.getRepositoryInstanceName().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            } else if (actionParameter.getMetadataKey().getParameterName().equalsIgnoreCase("labels")) {
                this.getRepositoryInstanceLabels().setInputValue(actionParameter.getValue(), getExecutionControl().getExecutionRuntime());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("name", this.getRepositoryReferenceName());
        this.getActionParameterOperationMap().put("repository", this.getRepositoryName());
        this.getActionParameterOperationMap().put("instance", this.getRepositoryInstanceName());
        this.getActionParameterOperationMap().put("labels", this.getRepositoryInstanceLabels());
    }

    protected boolean executeAction() throws SQLException, InterruptedException {
        String repositoryReferenceName = convertRepositoryReferenceName(getRepositoryReferenceName().getValue());
        String repositoryName = convertRepositoryName(getRepositoryName().getValue());
        String repositoryInstanceName = convertRepositoryInstanceName(getRepositoryInstanceName().getValue());
        List<String> repositoryInstanceLabels = convertRepositoryInstanceLabels(getRepositoryInstanceLabels().getValue());

        // Run the action
        //this.getExecutionControl().getExecutionRuntime().setRepository(this.getExecutionControl(), repositoryReferenceName,
        //        repositoryName, repositoryInstanceName, String.join(",", repositoryInstanceLabels));

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
                    .forEach(repositoryLabel -> labels.add(convertRepositoryInstanceLabel(DataTypeHandler.getInstance().resolve(repositoryLabel.trim(), getExecutionControl().getExecutionRuntime()))));
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