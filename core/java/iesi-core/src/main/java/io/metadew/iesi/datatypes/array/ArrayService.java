package io.metadew.iesi.datatypes.array;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.metadew.iesi.SpringContext;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.IDataTypeService;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

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
        List<String> splittedArguments = SpringContext.getBean(DataTypeHandler.class).splitInstructionArguments(arguments);
        List<DataType> resolvedArguments = splittedArguments.stream()
                .map(argument -> SpringContext.getBean(DataTypeHandler.class).resolve(argument, executionRuntime))
                .collect(Collectors.toList());
        return new Array(resolvedArguments);
    }

    @Override
    public boolean equals(Array _this, Array other, ExecutionRuntime executionRuntime) {
        if (_this == null && other == null) {
            return true;
        }
        if (_this == null || other == null) {
            return false;
        }
        if (_this.getList().size() != other.getList().size()) {
            return false;
        }

        for (int i = 0; i < _this.getList().size(); i++) {
            if (!SpringContext.getBean(DataTypeHandler.class).equals(_this.getList().get(i), other.getList().get(i), executionRuntime)) {
                return false;
            }
        }
        return true;
    }

    public Array resolve(DatasetImplementation databaseDatasetImplementation, String key, ArrayNode jsonNode, ExecutionRuntime executionRuntime) {
        Array array = new Array();
        int elementCounter = 1;
        for (JsonNode element : jsonNode) {
            array.add(SpringContext.getBean(DataTypeHandler.class).resolve(databaseDatasetImplementation, key + "." + elementCounter, element, executionRuntime));
            elementCounter++;
        }
        return array;
    }

}
