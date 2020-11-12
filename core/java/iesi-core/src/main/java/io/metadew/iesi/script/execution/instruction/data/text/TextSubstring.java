package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextSubstring implements DataInstruction {

    private final String FIRST_OPERATOR = "text";
    private final String SECOND_OPERATOR = "start";
    private final String THIRD_OPERATOR = "end";
    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile("(?<" + FIRST_OPERATOR + ">.+)," +
            "\\s*(?<" + SECOND_OPERATOR + ">-?\\d+)\\s*,\\s*(?<" + THIRD_OPERATOR + ">-?\\d+)\\s*");

    private final Pattern INPUT_PARAMETER_PATTERN_TWO_ARGUMENTS = Pattern.compile("(?<" + FIRST_OPERATOR + ">.+)," +
            "\\s*(?<" + SECOND_OPERATOR + ">-?\\d+)\\s*");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        Matcher inputParameterMatcherTwoArguments = INPUT_PARAMETER_PATTERN_TWO_ARGUMENTS.matcher(parameters);

        if (inputParameterMatcher.find()) {
            String text = inputParameterMatcher.group(FIRST_OPERATOR);
            int start = Integer.parseInt(inputParameterMatcher.group(SECOND_OPERATOR));
            int end = Integer.parseInt(inputParameterMatcher.group(THIRD_OPERATOR));

            if(start==0)
                start=1;

            if (start < 0)
                start += text.length()+1;

            if (end < 0)
                end += text.length()+1 ;

            verifyArguments(text, start-1, end);
            return text.substring(start-1, end);

        } else if (inputParameterMatcherTwoArguments.find()){

            String text = inputParameterMatcherTwoArguments.group(FIRST_OPERATOR);
            int start = Integer.parseInt(inputParameterMatcherTwoArguments.group(SECOND_OPERATOR));

            if(start==0)
                start=1;

            if (start < 0)
                start += text.length()+1;

            verifyArguments(text, start-1);
            return text.substring(start-1);

        } else {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
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
    private void verifyArguments(String text, int start) {
        if (start < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ". start {0} cannot be smaller than 0", start));
        }
    }

    @Override
    public String getKeyword() {
        return "text.substring";
    }
}
