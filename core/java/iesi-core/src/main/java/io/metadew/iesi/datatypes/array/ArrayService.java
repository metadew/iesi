package io.metadew.iesi.datatypes.array;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
@Log4j2
public class ArrayService implements IDataTypeService<Array> {

    private static ArrayService INSTANCE;

    public synchronized static ArrayService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArrayService();
        }
        return INSTANCE;
    }

    private ArrayService() {
    }

    @Override
    public Class<Array> appliesTo() {
        return Array.class;
    }

    @Override
    public String keyword() {
        return "list";
    }

    public Array resolve(String arguments, ExecutionRuntime executionRuntime) {
        log.trace(MessageFormat.format("resolving {0} for Array", arguments));
        arguments = executionRuntime.resolveVariables(arguments);
        List<String> splittedArguments = DataTypeHandler.getInstance().splitInstructionArguments(arguments);
        List<DataType> resolvedArguments = splittedArguments.stream()
                .map(argument -> DataTypeHandler.getInstance().resolve(argument, executionRuntime))
                .collect(Collectors.toList());
        return new Array(resolvedArguments);
    }

    public Array resolve(KeyValueDataset dataset, String key, ArrayNode jsonNode, ExecutionRuntime executionRuntime) throws IOException {
        Array array = new Array();
        int elementCounter = 1;
        for (JsonNode element : jsonNode) {
            array.add(DataTypeHandler.getInstance().resolve(dataset, key + "." + elementCounter, element, executionRuntime));
            elementCounter++;
        }
        return array;
    }

}
