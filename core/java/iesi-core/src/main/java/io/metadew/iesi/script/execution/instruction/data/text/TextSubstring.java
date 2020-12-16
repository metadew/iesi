package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextSubstring implements DataInstruction {

    private final static String FIRST_OPERATOR = "text";
    private final static String SECOND_OPERATOR = "start";
    private final static String THIRD_OPERATOR = "end";
    private final static Pattern THREE_ARGUMENTS_PATTERN = Pattern.compile("(?<" + FIRST_OPERATOR + ">.+)," +
            "\\s*(?<" + SECOND_OPERATOR + ">-?\\d+)\\s*,\\s*(?<" + THIRD_OPERATOR + ">-?\\d+)\\s*", Pattern.MULTILINE | Pattern.DOTALL);

    private final static Pattern TWO_ARGUMENTS_PATTERN = Pattern.compile("(?<" + FIRST_OPERATOR + ">.+)," +
            "\\s*(?<" + SECOND_OPERATOR + ">-?\\d+)\\s*", Pattern.MULTILINE | Pattern.DOTALL);

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = THREE_ARGUMENTS_PATTERN.matcher(parameters);
        Matcher inputParameterMatcherTwoArguments = TWO_ARGUMENTS_PATTERN.matcher(parameters);

        if (inputParameterMatcher.find()) {
            String text = inputParameterMatcher.group(FIRST_OPERATOR);
            int start = Integer.parseInt(inputParameterMatcher.group(SECOND_OPERATOR))-1;
            int end = Integer.parseInt(inputParameterMatcher.group(THIRD_OPERATOR));
            //if the user put a negative value, add length() and +1=> in order to take the 0 into account
            if (start < 0)
                start += text.length()+1;

            if (end < 0)
                end += text.length()+1 ;

            //This line must be after checking negative values
            int countLines = countNumberOfLines(text,end,inputParameterMatcher.groupCount());

            if(countLines!=0){
                start+= countLines;
                end+= countLines;

            }

            verifyArguments(text, start, end);
            return text.substring(start, end);

        } else if (inputParameterMatcherTwoArguments.find()){

            String text = inputParameterMatcherTwoArguments.group(FIRST_OPERATOR);
            int start = Integer.parseInt(inputParameterMatcherTwoArguments.group(SECOND_OPERATOR))-1;

            //if the user put a negative value, add length() and +1=> in order to take the 0 into account
            if (start < 0)
                start += text.length()+1;


            //This line must be after checking negative values
            int countLines = countNumberOfLines(text,start,inputParameterMatcherTwoArguments.groupCount());

            if(countLines!=0){
                start+= countLines;
            }

            verifyArguments(text, start);
            return text.substring(start);

        } else {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        }

    }

    private void verifyArguments(String text, int start, int end) {
        if (start < 0) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ". start {0} cannot be smaller or equal than 0", start));
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
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ". start {0} cannot be smaller or equal than 0", start));
        }
    }

    private int countNumberOfLines(String str, int index, int numberOfArguments){
        if (numberOfArguments==2){
            return str.substring(index).length() - str.substring(index).replaceAll("\n","").length();
        }
        return str.substring(0,index).length() - str.substring(0,index).replaceAll("\n","").length();
    }


    @Override
    public String getKeyword() {
        return "text.substring";
    }
}
