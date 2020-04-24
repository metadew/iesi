package io.metadew.iesi.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import io.metadew.iesi.datatypes.array.ArrayService;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDataset;
import io.metadew.iesi.datatypes.dataset.keyvalue.KeyValueDatasetService;
import io.metadew.iesi.datatypes.text.TextService;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DataTypeHandler {

    private final static String DatatypeStartCharacters = "{{";
    private final static String DatatypeStopCharacters = "}}";
    private final static Pattern DatatypePattern = Pattern.compile("\\^(?<datatype>\\w+)\\((?<arguments>.+)\\)");

    private Map<ClassStringPair, IDataTypeService> dataTypeServiceMap;

    private static DataTypeHandler INSTANCE;

    public synchronized static DataTypeHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataTypeHandler();
        }
        return INSTANCE;
    }

    private DataTypeHandler() {
        dataTypeServiceMap = new HashMap<>();
        dataTypeServiceMap.put(new ClassStringPair(TextService.getInstance().keyword(), TextService.getInstance().appliesTo()), TextService.getInstance());
        dataTypeServiceMap.put(new ClassStringPair(ArrayService.getInstance().keyword(), ArrayService.getInstance().appliesTo()), ArrayService.getInstance());
        dataTypeServiceMap.put(new ClassStringPair(KeyValueDatasetService.getInstance().keyword(), KeyValueDatasetService.getInstance().appliesTo()), KeyValueDatasetService.getInstance());
    }

    /*
        In case of multiple dataset types (keyvalue, resultset..) --> proposition dataset.kv and dataset.rs as keys
    */
    public DataType resolve(String input, ExecutionRuntime executionRuntime) {
        log.trace(MessageFormat.format("resolving {0} for datatype", input));
        if (input.startsWith(DatatypeStartCharacters) && input.endsWith(DatatypeStopCharacters)) {
            Matcher matcher = DatatypePattern.matcher(input.substring(DatatypeStartCharacters.length(), input.length() - DatatypeStopCharacters.length()));
            if (matcher.find()) {
                return getDataTypeService(matcher.group("datatype"))
                        .resolve(matcher.group("arguments"), executionRuntime);
//
//                switch (matcher.group("datatype")) {
//                    case "list":
//                        return ArrayService.getInstance().resolve(matcher.group("arguments"), executionRuntime);
//                    case "dataset":
//                        try {
//                            return KeyValueDatasetService.getInstance().resolve(matcher.group("arguments"), executionRuntime);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    default:
//                        throw new RuntimeException(MessageFormat.format("Input ''{0}'' does not have a correct datatype", input));
//                }
            } else {
                return TextService.getInstance().resolve(input, executionRuntime);
            }
        } else {
            return TextService.getInstance().resolve(input, executionRuntime);
        }
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

    public DataType resolve(KeyValueDataset rootDataset, String key, JsonNode jsonNode, ExecutionRuntime executionRuntime) throws IOException {
        if (jsonNode.getNodeType().equals(JsonNodeType.ARRAY)) {
            return ArrayService.getInstance().resolve(rootDataset, key, (ArrayNode) jsonNode, executionRuntime);
        } else if (jsonNode.getNodeType().equals(JsonNodeType.NULL)) {
            return TextService.getInstance().resolve((NullNode) jsonNode);
        } else if (jsonNode.isValueNode()) {
            return TextService.getInstance().resolve((ValueNode) jsonNode);
        }
        if (jsonNode.getNodeType().equals(JsonNodeType.OBJECT)) {
            return KeyValueDatasetService.getInstance().resolve(rootDataset, key, (ObjectNode) jsonNode, executionRuntime);
        } else {
            log.warn(MessageFormat.format("dataset.json.unknownnode=cannot decipher json node of type {0}", jsonNode.getNodeType().toString()));
        }
        return rootDataset;
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
