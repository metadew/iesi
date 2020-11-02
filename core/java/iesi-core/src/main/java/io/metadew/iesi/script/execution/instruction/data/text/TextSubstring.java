package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextSubstring implements DataInstruction {

    private final String FIRST_OPERATOR = "text";
    private final String SECOND_OPERATOR = "start";
    private final String THIRD_OPERATOR = "end";
    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile("(?<" + FIRST_OPERATOR + ">.+)," +
            "\\s*(?<" + SECOND_OPERATOR + ">-?\\d+)\\s*,\\s*(?<" + THIRD_OPERATOR + ">-?\\d+)\\s*");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            String text = inputParameterMatcher.group(FIRST_OPERATOR);
            int start = Integer.parseInt(inputParameterMatcher.group(SECOND_OPERATOR));
            int end = Integer.parseInt(inputParameterMatcher.group(THIRD_OPERATOR));
            if (start < 0) {
                start = text.length() + start;
            }
            if (end < 0) {
                end = text.length() + end;
            }
            verifyArguments(text, start, end);
            return text.substring(start, end);
        }
    }

    private void verifyArguments(String text, int start, int end) {
        if (start < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ". start {0} cannot be smaller than 0", start));
        }
        if (end > text.length()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ". end {0} cannot be greater than length of the text {1}", end, text.length()));
        }
        if (start > end) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ". start {0} cannot be greater than end {1}", start, end));
        }
    }

    public String substring(String string, int beginIndex, int endIndex){
        return string.substring(beginIndex-1,endIndex);
    }

    public String substring(String string, int beginIndex){
        String s1 = string.substring(beginIndex-1);
        List<String> collect = new ArrayList<>();

        collect = Arrays.asList(s1.split("\\b"))
                .stream().filter(s-> s.matches("\\w+"))
                .collect(Collectors.toList());

        return collect.get(0);
    }

    @Override
    public String getKeyword() {
        return "text.substring";
    }
}
