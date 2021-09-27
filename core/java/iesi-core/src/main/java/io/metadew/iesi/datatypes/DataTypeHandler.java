package io.metadew.iesi.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes._null.NullService;
import io.metadew.iesi.datatypes.array.ArrayService;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementation;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.datatypes.text.TextService;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DataTypeHandler {

    private static final String DATATYPE_START_CHARACTERS = "{{";
    private static final String DATATYPE_STOP_CHARACTERS = "}}";
    private static final Pattern DATATYPE_PATTERN = Pattern.compile("\\^(?<datatype>\\w+)\\((?<arguments>.*)\\)");

    private Map<ClassStringPair, IDataTypeService> dataTypeServiceMap;

    private static DataTypeHandler instance;

    public static synchronized DataTypeHandler getInstance() {
        if (instance == null) {
            instance = new DataTypeHandler();
        }
        return instance;
    }

    private DataTypeHandler() {
        dataTypeServiceMap = new HashMap<>();
        dataTypeServiceMap.put(new ClassStringPair(TextService.getInstance().keyword(), TextService.getInstance().appliesTo()), TextService.getInstance());
        dataTypeServiceMap.put(new ClassStringPair(ArrayService.getInstance().keyword(), ArrayService.getInstance().appliesTo()), ArrayService.getInstance());
        dataTypeServiceMap.put(new ClassStringPair(TemplateService.getInstance().keyword(), TemplateService.getInstance().appliesTo()), TemplateService.getInstance());
        dataTypeServiceMap.put(new ClassStringPair(DatasetImplementationHandler.getInstance().keyword(), DatasetImplementationHandler.getInstance().appliesTo()), DatasetImplementationHandler.getInstance());
        dataTypeServiceMap.put(new ClassStringPair(NullService.getInstance().keyword(), NullService.getInstance().appliesTo()), NullService.getInstance());
    }

    /*
        In case of multiple dataset types (keyvalue, resultset..) --> proposition dataset.kv and dataset.rs as keys
    */
    public DataType resolve(String input, ExecutionRuntime executionRuntime) {
        if (input == null) {
            return new Null();
        }

        input = executionRuntime.resolveVariables(input);
        input = executionRuntime.resolveConceptLookup(input).getValue();
        input = FrameworkCrypto.getInstance().resolve(input);

        log.trace(MessageFormat.format("resolving {0} for datatype", input));
        if (input.startsWith(DATATYPE_START_CHARACTERS) && input.endsWith(DATATYPE_STOP_CHARACTERS)) {
            Matcher matcher = DATATYPE_PATTERN.matcher(input.substring(DATATYPE_START_CHARACTERS.length(), input.length() - DATATYPE_STOP_CHARACTERS.length()));
            if (matcher.find()) {
                return getDataTypeService(matcher.group("datatype"))
                        .resolve(matcher.group("arguments"), executionRuntime);
            } else {
                return TextService.getInstance().resolve(input, executionRuntime);
            }
        } else {
            return TextService.getInstance().resolve(input, executionRuntime);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean equals(DataType _this, DataType other, ExecutionRuntime executionRuntime) {
        return getDataTypeService(_this.getClass()).equals(_this, other, executionRuntime);
    }

    public IDataTypeService getDataTypeService(String key) {
        return dataTypeServiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().keyword.equals(key))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find DataTypeService for " + key));
    }

    public IDataTypeService getDataTypeService(Class clazz) {
        return dataTypeServiceMap.entrySet().stream()
                .filter(entry -> entry.getKey().clazz.equals(clazz))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find DataTypeService for " + clazz.getSimpleName()));
    }

    public List<String> splitInstructionArguments(String argumentsString) {
        // TODO: move to Antler
        List<String> instructionArguments = new ArrayList<>();
        String instructionStart = "{{";
        String instructionStop = "}}";
        String argumentSeparator = ",";
        if (argumentsString == null) {
            return instructionArguments;
        }
        while (!argumentsString.isEmpty()) {
            int instructionStartIndex = argumentsString.indexOf(instructionStart);
            int argumentSeparatorIndex = argumentsString.indexOf(argumentSeparator);
            // only or last argument
            if (argumentSeparatorIndex == -1) {
                instructionArguments.add(argumentsString.trim());
                break;
            }
            // only simple arguments left or a simple argument before a function argument
            else if (instructionStartIndex == -1 || instructionStartIndex > argumentSeparatorIndex) {
                String[] splittedInstructionArguments = argumentsString.split(argumentSeparator, 2);
                instructionArguments.add(splittedInstructionArguments[0].trim());
                argumentsString = splittedInstructionArguments[1].trim();
            }

            // function argument before one or more other arguments
            else {
                int nextInstructionStartIndex = argumentsString.indexOf(instructionStart, instructionStartIndex + instructionStart.length());
                int instructionStopIndex = argumentsString.indexOf(instructionStop);
                while (nextInstructionStartIndex != -1 && nextInstructionStartIndex < instructionStopIndex) {
                    instructionStopIndex = argumentsString.indexOf(instructionStop, instructionStopIndex + instructionStop.length());
                    nextInstructionStartIndex = argumentsString.indexOf(instructionStart, nextInstructionStartIndex + instructionStart.length());
                }
                argumentSeparatorIndex = argumentsString.indexOf(argumentSeparator, instructionStopIndex + instructionStop.length());
                if (argumentSeparatorIndex == -1) {
                    instructionArguments.add(argumentsString.trim());
                    break;
                } else {
                    instructionArguments.add(argumentsString.substring(0, argumentSeparatorIndex));
                    argumentsString = argumentsString.substring(argumentSeparatorIndex + 1).trim();
                }
            }
        }
        return instructionArguments;
    }

    public DataType resolve(DatasetImplementation datasetImplementation, String key, JsonNode jsonNode, ExecutionRuntime executionRuntime) {
        if (jsonNode.getNodeType().equals(JsonNodeType.ARRAY)) {
            return ArrayService.getInstance().resolve(datasetImplementation, key, (ArrayNode) jsonNode, executionRuntime);
        } else if (jsonNode.getNodeType().equals(JsonNodeType.NULL)) {
            return new Null();
        } else if (jsonNode.isValueNode()) {
            return TextService.getInstance().resolve((ValueNode) jsonNode);
        }
        if (jsonNode.getNodeType().equals(JsonNodeType.OBJECT)) {
            return DatasetImplementationHandler.getInstance().resolve(datasetImplementation, key, (ObjectNode) jsonNode, executionRuntime);
        } else {
            log.warn(MessageFormat.format("dataset.json.unknownnode=cannot decipher json node of type {0}", jsonNode.getNodeType().toString()));
        }
        return datasetImplementation;
    }

    @RequiredArgsConstructor
    private static class ClassStringPair {

        private final String keyword;
        private final Class<? extends DataType> clazz;

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof ClassStringPair) {
                ClassStringPair other = (ClassStringPair) obj;
                return other.clazz.equals(clazz) || other.keyword.equals(keyword);
            } else if (obj instanceof String) {
                String other = (String) obj;
                return other.equals(keyword);
            } else if (obj instanceof Class) {
                Class other = (Class) obj;
                return other.equals(clazz);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz);
        }
    }

}
