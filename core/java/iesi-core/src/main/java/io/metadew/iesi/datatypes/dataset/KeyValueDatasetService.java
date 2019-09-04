package io.metadew.iesi.datatypes.dataset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyValueDatasetService {

    private final static Logger LOGGER = LogManager.getLogger();
    private final DataTypeService dataTypeService;
    private ExecutionRuntime executionRuntime;

    public KeyValueDatasetService(DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }

    private KeyValueDataset getObjectDataset(KeyValueDataset dataset, String keyPrefix, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        this.executionRuntime = executionRuntime;
        if (keyPrefix != null) {
            List<String> labels = new ArrayList<>(dataset.getLabels());
            labels.add(keyPrefix);
            return new KeyValueDataset(dataset.getName(), labels, executionRuntime);
        } else {
            return dataset;
        }
    }

    public void writeRawJSON(KeyValueDataset dataset, String json) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(json);
            writeRawJSON(dataset, jsonNode);
        } catch (IOException e) {
            // LOGGER.warn(MessageFormat.format("dataset.json.unknownnode=cannot decipher json node of type {0}", field.getValue().getNodeType().toString()));
        }
    }

    public void writeRawJSON(KeyValueDataset dataset, JsonNode jsonNode) {
        LOGGER.debug(MessageFormat.format("writing raw json ''{0}'' to dataset {1}", jsonNode.toString(), dataset.toString()));
        writeRawJSON(dataset, jsonNode, "");
    }

    private void writeRawJSON(KeyValueDataset dataset, JsonNode jsonNode, String keyPrefix) {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getValue().getNodeType().equals(JsonNodeType.OBJECT)) {
                writeRawJSON(dataset, field.getValue(), keyPrefix + field.getKey() + ".");
            } else if (field.getValue().getNodeType().equals(JsonNodeType.ARRAY)) {
                int arrayCounter = 1;
                for (JsonNode element : field.getValue()) {
                    writeRawJSON(dataset, element, keyPrefix + field.getKey() + "." + arrayCounter + ".");
                    arrayCounter++;
                }
            } else if (field.getValue().getNodeType().equals(JsonNodeType.NULL)) {
                dataset.setDataItem(keyPrefix + field.getKey(), new Text(""));
            } else if (field.getValue().isValueNode()) {
                dataset.setDataItem(keyPrefix + field.getKey(), new Text(field.getValue().asText()));
            } else {
                LOGGER.warn(MessageFormat.format("dataset.json.unknownnode=cannot decipher json node of type {0}", field.getValue().getNodeType().toString()));
            }
        }
    }

    public Dataset resolve(String arguments) throws IOException, SQLException {
        List<String> splittedArguments = dataTypeService.splitInstructionArguments(arguments);
        if (splittedArguments.size() == 2) {
            List<DataType> resolvedArguments = splittedArguments.stream()
                    .map(dataTypeService::resolve)
                    .collect(Collectors.toList());
            return new KeyValueDataset(resolvedArguments.get(0), resolvedArguments.get(1), executionRuntime);
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot create dataset with arguments ''{0}''", splittedArguments.toString()));
        }
    }


    public void write(KeyValueDataset dataset, ObjectNode jsonNode) throws IOException, SQLException {
        dataTypeService.resolve(dataset, null, jsonNode);
    }

    public DataType resolve(KeyValueDataset dataset, String key, ObjectNode jsonNode) throws IOException, SQLException {
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        KeyValueDataset objectDataset = getObjectDataset(dataset, key, executionRuntime);
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            DataType object = dataTypeService.resolve(objectDataset, field.getKey(), field.getValue());
            objectDataset.setDataItem(field.getKey(), object);
        }
        return objectDataset;
    }
}
