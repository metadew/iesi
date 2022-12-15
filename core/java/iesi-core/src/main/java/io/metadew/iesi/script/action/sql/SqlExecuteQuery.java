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
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
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
import java.util.Optional;

@Log4j2
public class SqlExecuteQuery extends ActionTypeExecution {

    // Parameters
    private final String QUERY_KEY = "query";
    private final String CONNECTION_KEY = "connection";
    private final String OUTPUT_DATASET_KEY = "outputDataset";
    private final String OUTPUT_RESULT = "outputResult";
    private final DatabaseHandler databaseHandler = SpringContext.getBean(DatabaseHandler.class);
    private final ConnectionConfiguration connectionConfiguration = SpringContext.getBean(ConnectionConfiguration.class);

    private DatasetImplementation outputDataset;

    public SqlExecuteQuery(ExecutionControl executionControl,
                           ScriptExecution scriptExecution, ActionExecution actionExecution) {
        super(executionControl, scriptExecution, actionExecution);
    }

    public void prepareAction() {
    }

    protected boolean executeAction() throws SQLException, InterruptedException {
        String query = convertQuery(getParameterResolvedValue(QUERY_KEY));
        String connectionName = convertConnectionName(getParameterResolvedValue(CONNECTION_KEY));
        boolean outputResult = convertOutputResult(getParameterResolvedValue(OUTPUT_RESULT));
        // Get Connection
        Connection connection = connectionConfiguration
                .get(new ConnectionKey(connectionName, this.getExecutionControl().getEnvName()))
                .orElseThrow(() -> new RuntimeException("Unknown connection name: " + connectionName));

        Database database = databaseHandler.getDatabase(connection);

        if (!query.trim().endsWith(";")) {
            query = query + ";";
        }

        outputDataset = convertOutputDatasetReferenceName(getParameterResolvedValue(OUTPUT_DATASET_KEY));
        try {
            CachedRowSet crs = databaseHandler.executeQuery(database, query);
            ArrayNode result = SpringContext.getBean(SqlResultService.class).convert(crs);

            outputResponse(result);

            this.getActionExecution().getActionControl().increaseSuccessCount();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private void outputResponse(ArrayNode result) {
        Optional<DatasetImplementation> outputDataset = getOutputDataset();
        if (outputDataset.isPresent()) {
            if (!DatasetImplementationHandler.getInstance().isEmpty(outputDataset.get())) {
                log.warn(String.format("Output dataset %s already contains data items. Clearing old data items before writing output", outputDataset.get()));
                DatasetImplementationHandler.getInstance().clean(outputDataset.get(), getExecutionControl().getExecutionRuntime());
            }
            SpringContext.getBean(SqlResultService.class).writeToDataset(outputDataset.get(), result, this.getExecutionControl().getExecutionRuntime());
        }

        SpringContext.getBean(SqlResultService.class).traceOutput(result,this.getActionExecution().getActionControl());
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

    private boolean convertOutputResult(DataType dataType) {
        if (dataType == null || dataType instanceof Null) {
            return false;
        } else if (dataType instanceof Text) {
            if (((Text) dataType).getString().equalsIgnoreCase("Y")) {
                return true;
            } else {
                return false;
            }
        } else {
            log.warn(MessageFormat.format(getActionExecution().getAction().getType().concat(" does not accept {0} as type for certificates parameter"),
                    dataType.getClass()));
            return false;
        }
    }

    private Optional<DatasetImplementation> getOutputDataset() {
        return Optional.ofNullable(outputDataset);
    }

    @Override
    protected String getKeyword() {
        return "sql.executeQuery";
    }


}
