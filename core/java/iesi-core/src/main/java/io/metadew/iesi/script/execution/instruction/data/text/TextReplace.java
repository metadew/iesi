package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextReplace implements DataInstruction {

    private final static String FIRST_OPERATOR = "text";
    private final static String SECOND_OPERATOR = "characterToBeReplaced";
    private final static String THIRD_OPERATOR = "replacementCharacter";

    private final static Pattern THREE_ARGUMENTS_PATTERN = Pattern.compile("\\s*\"(?<" + FIRST_OPERATOR + ">.+)\"\\s*,\\s*\"(?<" + SECOND_OPERATOR + ">.+)\"\\s*,\\s*\"(?<" + THIRD_OPERATOR + ">.+)\"\\s*");

    private final static Pattern TWO_ARGUMENTS_PATTERN = Pattern.compile("\\s*\"(?<" + FIRST_OPERATOR + ">.+)\"\\s*,\\s*\"(?<" + SECOND_OPERATOR + ">.+)\"\\s*");

    @Override
    public String generateOutput(String parameters) {

        Matcher inputParameterMatcher = THREE_ARGUMENTS_PATTERN.matcher(parameters);
        Matcher inputParameterMatcherTwoArguments = TWO_ARGUMENTS_PATTERN.matcher(parameters);

        if (inputParameterMatcher.find()) {
            String text = inputParameterMatcher.group(FIRST_OPERATOR);
            String characterToBeReplaced = inputParameterMatcher.group(SECOND_OPERATOR);
            String replacementCharacter = inputParameterMatcher.group(THIRD_OPERATOR);
            text = text.replace(characterToBeReplaced, replacementCharacter);
            return text;
        } else if (inputParameterMatcherTwoArguments.matches()) {
            String text = inputParameterMatcherTwoArguments.group(FIRST_OPERATOR);
            String characterToBeReplaced = inputParameterMatcherTwoArguments.group(SECOND_OPERATOR);
            text = text.replace(characterToBeReplaced, "");
            return text;
        } else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), parameters));
        }
    }

    @Override
    public String getKeyword() {
        return "text.replace";
    }

}