package io.metadew.iesi.connection.tools.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementation;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ActionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.sql.rowset.CachedRowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@Service
@Log4j2
public class SqlResultService {

    private final DataTypeHandler dataTypeHandler;
    private final ObjectMapper objectMapper;

    public SqlResultService(DataTypeHandler dataTypeHandler) {
        this.dataTypeHandler = dataTypeHandler;
        this.objectMapper = new ObjectMapper();
    }

    public ArrayNode convert(CachedRowSet crs) {
        log.info("Converting the first 100 sql rows into an array of json object");
        try {
            int counter = 100;
            ArrayNode rootNode = objectMapper.createArrayNode();
            ResultSetMetaData rsmd = crs.getMetaData();
            while (crs.next() && counter > 0) {
                ObjectNode objectNode = objectMapper.createObjectNode();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String columnName = rsmd.getColumnName(i + 1);
                    Object value = crs.getObject(i + 1);
                    if (value == null) {
                        objectNode.putNull(columnName);
                    } else {
                        objectNode.put(columnName, value.toString());
                    }
                }
                rootNode.add(objectNode);
                counter -= 1;
            }
            return rootNode;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void writeToDataset(DatasetImplementation datasetImplementation, ArrayNode jsonValue, ExecutionRuntime executionRuntime) {
        if (datasetImplementation instanceof InMemoryDatasetImplementation) {
            DatasetImplementationHandler.getInstance().setDataItem(datasetImplementation, "rows", new Text(Integer.toString(jsonValue.size())));
            DatasetImplementationHandler.getInstance().setDataItem(datasetImplementation, "rawQueryResult", new Text(jsonValue.toString()));
            DatasetImplementationHandler.getInstance().setDataItem(datasetImplementation, "queryResult", dataTypeHandler.resolve(datasetImplementation, "queryResult", jsonValue, executionRuntime));
        } else {
            log.warn("SQL results can only be stored in a memory dataset");
        }
    }

    public void traceOutput(ArrayNode result, ActionControl actionControl) {
        actionControl.logOutput("sys.out", "data.query.complete");
        actionControl.logOutput("sql.execute.size", Integer.toString(result.size()));
        actionControl.logOutput("sql.rows", result.toPrettyString());
    }
}
