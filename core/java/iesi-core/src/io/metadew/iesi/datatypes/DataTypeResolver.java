package io.metadew.iesi.datatypes;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataTypeResolver {

    private static String DatatypeStartCharacters = "{{";
    private static String DatatypeStopCharacters = "}}";
    private static Pattern DatatypePattern = Pattern.compile("\\^(?<datatype>\\w+)\\((?<arguments>.+)\\)");


    public static DataType resolveToDatatype(String input) {
        if (input.startsWith(DatatypeStartCharacters) && input.endsWith(DatatypeStopCharacters)) {
            Matcher matcher = DatatypePattern.matcher(input.substring(DatatypeStartCharacters.length(), input.length() - DatatypeStopCharacters.length()));
            if (matcher.find()) {
                switch (matcher.group("datatype")) {
                    case "list":
                        return resolveToList(matcher.group("arguments"));
                    case "dataset":
                        return resolveToDataset(matcher.group("arguments"));
                    default:
                        throw new RuntimeException(MessageFormat.format("Input '{0}' does not have a correct datatype", input));
                }
            } else {
                throw new RuntimeException(MessageFormat.format("Input '{0}' does not have a correct datatype", input));
            }
        } else {
            return new Text(input);
        }
    }

    private static Dataset resolveToDataset(String arguments) {
        List<String> splittedArguments = splitInstructionArguments(arguments);
        if (splittedArguments.size() == 2) {
            List<DataType> resolvedArguments = splittedArguments.stream()
                    .map(DataTypeResolver::resolveToDatatype)
                    .collect(Collectors.toList());
            return new Dataset(resolvedArguments.get(0), resolvedArguments.get(1));
        } else {
            throw new RuntimeException(MessageFormat.format("Cannot create dataset with arguments '{0}'", splittedArguments.toString()));
        }
    }

    private static Array resolveToList(String arguments) {
        List<String> splittedArguments = splitInstructionArguments(arguments);
        List<DataType> resolvedArguments = splittedArguments.stream()
                .map(DataTypeResolver::resolveToDatatype)
                .collect(Collectors.toList());
        return new Array(resolvedArguments);
    }

    private static List<String> splitInstructionArguments(String argumentsString) {
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

}
