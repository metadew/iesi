package io.metadew.iesi.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import io.metadew.iesi.datatypes.array.ArrayService;
import io.metadew.iesi.datatypes.dataset.KeyValueDataset;
import io.metadew.iesi.datatypes.dataset.KeyValueDatasetService;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.datatypes.text.TextService;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTypeService {

    private final static Logger LOGGER = LogManager.getLogger();

    private static String DatatypeStartCharacters = "{{";
    private static String DatatypeStopCharacters = "}}";
    private static Pattern DatatypePattern = Pattern.compile("\\^(?<datatype>\\w+)\\((?<arguments>.+)\\)");

    private KeyValueDatasetService keyValueDatasetService;
    private ArrayService arrayService;
    private TextService textService;


    public DataTypeService(ExecutionRuntime executionRuntime) {
        this.keyValueDatasetService = new KeyValueDatasetService(this, executionRuntime);
        this.arrayService = new ArrayService(this);
        this.textService = new TextService();
    }

    /*
        In case of multiple dataset types (keyvalue, resultset..) --> proposition dataset.kv and dataset.rs as keys
    */
    public DataType resolve(String input) {
        if (input.startsWith(DatatypeStartCharacters) && input.endsWith(DatatypeStopCharacters)) {
            Matcher matcher = DatatypePattern.matcher(input.substring(DatatypeStartCharacters.length(), input.length() - DatatypeStopCharacters.length()));
            if (matcher.find()) {
                switch (matcher.group("datatype")) {
                    case "list":
                        return arrayService.resolve(matcher.group("arguments"));
                    case "dataset":
                        try {
                            return keyValueDatasetService.resolve(matcher.group("arguments"));
                        } catch (IOException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                    default:
                        throw new RuntimeException(MessageFormat.format("Input ''{0}'' does not have a correct datatype", input));
                }
            } else {
                return new Text(input);
            }
        } else {
            return new Text(input);
        }
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

    public DataType resolve(KeyValueDataset rootDataset, String key, JsonNode jsonNode) throws IOException, SQLException {
        if (jsonNode.getNodeType().equals(JsonNodeType.ARRAY)) {
            return arrayService.resolve(rootDataset, key, (ArrayNode) jsonNode);
        } else if (jsonNode.getNodeType().equals(JsonNodeType.NULL)) {
            return textService.resolve((NullNode) jsonNode);
        } else if (jsonNode.isValueNode()) {
            return textService.resolve((ValueNode) jsonNode);
        } if (jsonNode.getNodeType().equals(JsonNodeType.OBJECT)) {
            return keyValueDatasetService.resolve(rootDataset, key, (ObjectNode) jsonNode);
        } else {
            LOGGER.warn(MessageFormat.format("dataset.json.unknownnode=cannot decipher json node of type {0}", jsonNode.getNodeType().toString()));
        }
        return rootDataset;
    }

    public KeyValueDatasetService getKeyValueDatasetService() {
        return keyValueDatasetService;
    }

    public ArrayService getArrayService() {
        return arrayService;
    }

    public TextService getTextService() {
        return textService;
    }

}
