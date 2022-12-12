package io.metadew.iesi.connection.tools.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.springframework.stereotype.Service;

import javax.sql.rowset.CachedRowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@Service
public class SqlResultService {

    private final DataTypeHandler dataTypeHandler;
    private final ObjectMapper objectMapper;

    public SqlResultService(DataTypeHandler dataTypeHandler) {
        this.dataTypeHandler = dataTypeHandler;
        this.objectMapper = new ObjectMapper();
    }

    public ArrayNode convert(CachedRowSet crs) {
        try {
            ArrayNode rootNode = objectMapper.createArrayNode();
            ResultSetMetaData rsmd = crs.getMetaData();
            while (crs.next()) {
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
            }
            return rootNode;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void writeToDataset(DatasetImplementation datasetImplementation, ArrayNode jsonValue, ExecutionRuntime executionRuntime) {
        DatasetImplementationHandler.getInstance().setDataItem(datasetImplementation, "rows", new Text(Integer.toString(jsonValue.size())));
        DatasetImplementationHandler.getInstance().setDataItem(datasetImplementation, "rawQueryResult", new Text(jsonValue.toString()));
        dataTypeHandler.resolve(datasetImplementation, "row", jsonValue, executionRuntime);
    }
}
