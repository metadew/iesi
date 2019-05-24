package io.metadew.iesi.script.action;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionParameter;
import io.metadew.iesi.metadata.definition.Dataset;
import io.metadew.iesi.metadata.definition.DatasetParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import org.apache.logging.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SqlDefineDataset {

    private ActionExecution actionExecution;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;

    // Parameters
    private ActionParameterOperation referenceName;
    private ActionParameterOperation sqlStatement;
    private ActionParameterOperation sqlIdentifier;
    private HashMap<String, ActionParameterOperation> actionParameterOperationMap;

    // Constructors
    public SqlDefineDataset() {

    }

    public SqlDefineDataset(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
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
        // Set Parameters
        this.setReferenceName(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "name"));
        this.setSqlStatement(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "statement"));
        this.setSqlIdentifier(new ActionParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(),
                this.getActionExecution(), this.getActionExecution().getAction().getType(), "identifier"));

        // Get Parameters
        for (ActionParameter actionParameter : this.getActionExecution().getAction().getParameters()) {
            if (actionParameter.getName().equalsIgnoreCase("name")) {
                this.getReferenceName().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("statement")) {
                this.getSqlStatement().setInputValue(actionParameter.getValue());
            } else if (actionParameter.getName().equalsIgnoreCase("identifier")) {
                this.getSqlIdentifier().setInputValue(actionParameter.getValue());
            }
        }

        // Create parameter list
        this.getActionParameterOperationMap().put("name", this.getReferenceName());
        this.getActionParameterOperationMap().put("statement", this.getSqlStatement());
        this.getActionParameterOperationMap().put("identifier", this.getSqlIdentifier());
    }

    public boolean execute() {
        try {
            String referenceName = convertReferenceName(getReferenceName().getValue());
            String statement = convertStatement(getReferenceName().getValue());
            String connection = convertIdentifierName(getReferenceName().getValue());
            return execute(referenceName, statement, connection);

        } catch (Exception e) {
            StringWriter StackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(StackTrace));

            this.getActionExecution().getActionControl().increaseErrorCount();

            this.getActionExecution().getActionControl().logOutput("exception", e.getMessage());
            this.getActionExecution().getActionControl().logOutput("stacktrace", StackTrace.toString());

            return false;
        }

    }

    private boolean execute(String referenceName, String statement, String identifier) {
        Dataset dataset = new Dataset();
        dataset.setType("sql");
        dataset.setName(referenceName);

        // Parameters
        List<DatasetParameter> datasetParameters = new ArrayList<>();
        DatasetParameter datasetParameter = new DatasetParameter();
        datasetParameter.setName("statement");
        datasetParameter.setValue(statement);
        datasetParameters.add(datasetParameter);

        datasetParameter.setName("identifier");
        datasetParameter.setValue(identifier);
        datasetParameters.add(datasetParameter);

        dataset.setParameters(datasetParameters);

        // this.getExecutionControl().getExecutionRuntime().setKeyValueDataset(referenceName, dataset);

        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    private String convertIdentifierName(DataType identifierName) {
        // TODO: list?
        if (identifierName instanceof Text) {
            return identifierName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for identifierName",
                    identifierName.getClass()), Level.WARN);
            return identifierName.toString();
        }
    }

    private String convertStatement(DataType statementName) {
        if (statementName instanceof Text) {
            return statementName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for statementName",
                    statementName.getClass()), Level.WARN);
            return statementName.toString();
        }
    }

    private String convertReferenceName(DataType referenceName) {
        if (referenceName instanceof Text) {
            return referenceName.toString();
        } else {
            this.getFrameworkExecution().getFrameworkLog().log(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for referenceName",
                    referenceName.getClass()), Level.WARN);
            return referenceName.toString();
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

    public ActionParameterOperation getActionParameterOperation(String key) {
        return this.getActionParameterOperationMap().get(key);
    }

    public ActionParameterOperation getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(ActionParameterOperation sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    public ActionParameterOperation getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(ActionParameterOperation referenceName) {
        this.referenceName = referenceName;
    }

    public ActionParameterOperation getSqlIdentifier() {
        return sqlIdentifier;
    }

    public void setSqlIdentifier(ActionParameterOperation sqlIdentifier) {
        this.sqlIdentifier = sqlIdentifier;
    }

}
