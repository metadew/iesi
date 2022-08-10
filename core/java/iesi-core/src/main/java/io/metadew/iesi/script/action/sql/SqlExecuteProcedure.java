package io.metadew.iesi.script.action.sql;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.script.action.ActionTypeExecution;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ScriptExecution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

public class SqlExecuteProcedure extends ActionTypeExecution {

    private static final String SQL_PROCEDURE_KEY = "procedure";
    private static final String CONNECTION_NAME_KEY = "connection";
    private static final String SQL_PARAMETERS_KEY = "parameters";
    private static final String OUTPUT_DATASET_KEY = "outputDataset";
    private static final String APPEND_OUTPUT_KEY = "appendOutput";
    private static final Logger LOGGER = LogManager.getLogger();

    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);

    public SqlExecuteProcedure(ExecutionControl executionControl,
                               ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() { }

    protected boolean executeAction() throws SQLException, InterruptedException {

        String sqlProcedure = convertSqlProcedure(getParameterResolvedValue(SQL_PROCEDURE_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_NAME_KEY));
        String sqlParameters = convertSqlParameters(getParameterResolvedValue(SQL_PARAMETERS_KEY));
        String outputDatasetReferenceName = convertDatasetReferenceName(getParameterResolvedValue(OUTPUT_DATASET_KEY));
        boolean appendOutput = convertAppendOutput(getParameterResolvedValue(APPEND_OUTPUT_KEY));

        // Get Connection
        Connection connection = ConnectionConfiguration.getInstance()
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .orElseThrow(() -> new RuntimeException("Unknown connection name: " + connectionName));

        Database database = databaseHandler.getDatabase(connection);

        SqlScriptResult sqlScriptResult = null;
        CachedRowSet crs = null;
        crs = databaseHandler.executeProcedure(database, sqlProcedure, sqlParameters);
        // TODO resolve for files and resolve inside
        // TODO Retrieve config from a file

        if (!outputDatasetReferenceName.isEmpty()) {
            Optional<DatasetImplementation> dataset = getExecutionControl().getExecutionRuntime().getDataset(outputDatasetReferenceName);

            // Perform the action
            //SQLDataTransfer.transferData(crs, dataset.get().getDatasetDatabase(), dataset.get().getName(), !appendOutput);
            sqlScriptResult = new SqlScriptResult(0, "data.transfer.complete", "");

        } else {
            sqlScriptResult = new SqlScriptResult(0, "sql.procedure.complete", "");
        }

        // Evaluate result
        this.getActionExecution().getActionControl().logOutput("sys.out", sqlScriptResult.getSystemOutput());

        if (sqlScriptResult.getReturnCode() != 0) {
            this.getActionExecution().getActionControl().logOutput("err.out", sqlScriptResult.getErrorOutput());
            throw new RuntimeException("Error execting SQL query");
        }

        this.getActionExecution().getActionControl().increaseSuccessCount();
        return true;
    }

    @Override
    protected String getKeyword() {
        return "sql.executeProcedure";
    }

    private String convertSqlProcedure(DataType sqlProcedure) {
        if (sqlProcedure instanceof Text) {
            return sqlProcedure.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sqlProcedure",
                    sqlProcedure.getClass()));
            return sqlProcedure.toString();
        }
    }

    private String convertSqlParameters(DataType sqlParameters) {
        if (sqlParameters instanceof Text) {
            return sqlParameters.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for sqlParameters",
                    sqlParameters.getClass()));
            return sqlParameters.toString();
        }
    }

    private String convertConnectionName(DataType connectionName) {
        if (connectionName instanceof Text) {
            return connectionName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for connection name",
                    connectionName.getClass()));
            return connectionName.toString();
        }
    }

    private boolean convertAppendOutput(DataType appendOutput) {
        if (appendOutput instanceof Text) {
            return appendOutput.toString().equalsIgnoreCase("y");
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for appendOutput",
                    appendOutput.getClass()));
            return false;
        }
    }

    private String convertDatasetReferenceName(DataType datasetReferenceName) {
        if (datasetReferenceName instanceof Text) {
            return datasetReferenceName.toString();
        } else {
            LOGGER.warn(MessageFormat.format(this.getActionExecution().getAction().getType() + " does not accept {0} as type for dataset reference name",
                    datasetReferenceName.getClass()));
            return datasetReferenceName.toString();
        }
    }
}
