package io.metadew.iesi.script.action.fwk;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FwkSetRepository extends ActionTypeExecution {

    private static final String REPOSITORY_REFERENCE_NAME_KEY = "name";
    private static final String REPOSITORY_NAME_KEY = "repository";
    private static final String REPOSITORY_INSTANCE_NAME_KEY = "instance";
    private static final String REPOSITORY_INSTANCE_LABELS_KEY = "labels";
    private static final Logger LOGGER = LogManager.getLogger();

    public FwkSetRepository(ExecutionControl executionControl,
                            ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() { }

    protected boolean executeAction() throws SQLException, InterruptedException {
        String repositoryReferenceName = convertRepositoryReferenceName(getParameterResolvedValue(REPOSITORY_REFERENCE_NAME_KEY));
        String repositoryName = convertRepositoryName(getParameterResolvedValue(REPOSITORY_NAME_KEY));
        String repositoryInstanceName = convertRepositoryInstanceName(getParameterResolvedValue(REPOSITORY_INSTANCE_NAME_KEY));
        List<String> repositoryInstanceLabels = convertRepositoryInstanceLabels(getParameterResolvedValue(REPOSITORY_INSTANCE_LABELS_KEY));

        // Run the action
        //this.getExecutionControl().getExecutionRuntime().setRepository(this.getExecutionControl(), repositoryReferenceName,
        //        repositoryName, repositoryInstanceName, String.join(",", repositoryInstanceLabels));

        this.getActionExecution().getActionControl().increaseSuccessCount();

        return true;
    }

    @Override
    protected String getKeyword() {
        return "fwk.setRepository";
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
                    .forEach(repositoryLabel -> labels.add(convertRepositoryInstanceLabel(SpringContext.getBean(DataTypeHandler.class).resolve(repositoryLabel.trim(), getExecutionControl().getExecutionRuntime()))));
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

}