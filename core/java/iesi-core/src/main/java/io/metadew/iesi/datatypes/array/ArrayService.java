package io.metadew.iesi.datatypes.array;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.datatypes.dataset.KeyValueDataset;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayService {

    private DataTypeService dataTypeService;

    public ArrayService(DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }

    public Array resolve(String arguments) {
        List<String> splittedArguments = dataTypeService.splitInstructionArguments(arguments);
        List<DataType> resolvedArguments = splittedArguments.stream()
                .map(argument -> dataTypeService.resolve(argument))
                .collect(Collectors.toList());
        return new Array(resolvedArguments);
    }

    public Array resolve(KeyValueDataset dataset, String key, ArrayNode jsonNode) throws IOException, SQLException {
        Array array = new Array();
        int elementCounter = 1;
        for (JsonNode element : jsonNode) {
            array.add(dataTypeService.resolve(dataset, key + "." + elementCounter, element));
            elementCounter++;
        }
        return array;
    }

}
