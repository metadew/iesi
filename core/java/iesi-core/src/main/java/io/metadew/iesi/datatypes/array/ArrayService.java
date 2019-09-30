package io.metadew.iesi.datatypes.array;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.dataset.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayService {

    private static final Logger LOGGER = LogManager.getLogger();
    private DataTypeService dataTypeService;

    public ArrayService(DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }

    public Array resolve(String arguments, ExecutionRuntime executionRuntime) {
        LOGGER.trace(MessageFormat.format("resolving {0} for Array", arguments));
        arguments = executionRuntime.resolveVariables(arguments);
        List<String> splittedArguments = dataTypeService.splitInstructionArguments(arguments);
        List<DataType> resolvedArguments = splittedArguments.stream()
                .map(argument -> dataTypeService.resolve(argument, executionRuntime))
                .collect(Collectors.toList());
        return new Array(resolvedArguments);
    }

    public Array resolve(KeyValueDataset dataset, String key, ArrayNode jsonNode, ExecutionRuntime executionRuntime) throws IOException, SQLException {
        Array array = new Array();
        int elementCounter = 1;
        for (JsonNode element : jsonNode) {
            array.add(dataTypeService.resolve(dataset, key + "." + elementCounter, element, executionRuntime));
            elementCounter++;
        }
        return array;
    }

}
