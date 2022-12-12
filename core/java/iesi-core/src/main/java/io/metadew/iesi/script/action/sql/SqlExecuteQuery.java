package io.metadew.iesi.script.action.sql;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.tools.sql.SqlResultService;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import lombok.extern.log4j.Log4j2;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;

@Log4j2
public class SqlExecuteQuery extends ActionTypeExecution {

    // Parameters
    private static final String QUERY_KEY = "query";
    private static final String CONNECTION_KEY = "connection";
    private static final String APPEND_OUTPUT_KEY = "appendOutput";
    private static final String OUTPUT_DATASET_KEY = "outputDataset";

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);
    private final ConnectionConfiguration connectionConfiguration = SpringContext.getBean(ConnectionConfiguration.class);

    public SqlExecuteQuery(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            log.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private String convertQuery(DataType query) {
        if (query instanceof Text) {
            return query.toString();
        } else {
            log.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for query",
                    query.getClass()));
            return query.toString();
        }
    }

    private boolean convertAppendOutput(DataType appendOutput) {
        if (appendOutput == null || appendOutput instanceof Null){
            return false;
        }
        else if (appendOutput instanceof Text) {
            return appendOutput.toString().equalsIgnoreCase("y");
        } else {
            log.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for appendOutput",
                    appendOutput.getClass()));
            return false;
        }
    }

    protected boolean executeAction() throws SQLException, InterruptedException {
        String query = convertQuery(getParameterResolvedValue(QUERY_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_KEY));
        boolean appendOutput = convertAppendOutput(getParameterResolvedValue(APPEND_OUTPUT_KEY));
        // Get Connection
        Connection connection = connectionConfiguration
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .orElseThrow(() -> new RuntimeException("Unknown connection name: " + connectionName));

        Database database = databaseHandler.getDatabase(connection);

        // Run the action
        // Make sure the SQL statement is ended with a ;
        if (!query.trim().endsWith(";")) {
            query = query + ";";
        }

        SqlScriptResult sqlScriptResult;

        DatasetImplementation dataset = convertOutputDatasetReferenceName(getParameterResolvedValue(OUTPUT_DATASET_KEY));
        CachedRowSet crs = databaseHandler.executeQuery(database, query);
        this.getActionExecution().getActionControl().logOutput("sql.execute.size", Integer.toString(crs.size()));
        // TODO resolve for files and resolve inside
        if (dataset != null) {

            ArrayNode result = SpringContext.getBean(SqlResultService.class).convert(crs);
            SpringContext.getBean(SqlResultService.class).writeToDataset(dataset, result, this.getExecutionControl().getExecutionRuntime());
        }

        sqlScriptResult = new SqlScriptResult(0, "sql.execute.complete", "");

        // Evaluate result
        this.getActionExecution().getActionControl().logOutput("sys.out", sqlScriptResult.getSystemOutput());

        if (sqlScriptResult.getReturnCode() != 0) {
            this.getActionExecution().getActionControl().logOutput("err.out", sqlScriptResult.getErrorOutput());
            throw new RuntimeException("Error execting SQL query");
        }

        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    private DatasetImplementation convertOutputDatasetReferenceName(DataType outputDatasetReferenceName) {
        if (outputDatasetReferenceName == null || outputDatasetReferenceName instanceof Null) {
            return null;
        } else if (outputDatasetReferenceName instanceof Text) {
            return getExecutionControl().getExecutionRuntime()
                    .getDataset(((Text) outputDatasetReferenceName).getString())
                    .orElseThrow(() -> new RuntimeException(MessageFormat.format("No dataset found with name ''{0}''", ((Text) outputDatasetReferenceName).getString())));
        } else if (outputDatasetReferenceName instanceof DatasetImplementation) {
            return (DatasetImplementation) outputDatasetReferenceName;
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType() + " does not accept {0} as type for OutputDatasetReferenceName",
                    outputDatasetReferenceName.getClass()));
            throw new RuntimeException(MessageFormat.format("Output dataset does not allow type ''{0}''", outputDatasetReferenceName.getClass()));
        }
    }

    @Override
    protected String getKeyword() {
        return "sql.executeQuery";
    }


}
