package io.metadew.iesi.script.execution.instruction.data.math;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractOperationTwoArg implements DataInstruction {

    private final String FIRSTOPERATOR = "FirstOperator";
    private final String SECONDOPERATOR = "SecondOperator";
    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile("\\s*(?<" + FIRSTOPERATOR + ">\\d+(.\\d+)?)\\s*,\\s*(?<"
            + SECONDOPERATOR + ">\\d+(.\\d+)?)\\s*");


    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            String firstOperator = inputParameterMatcher.group(FIRSTOPERATOR);
            String secondOperator = inputParameterMatcher.group(SECONDOPERATOR);
            return handleOperationAsStrings(firstOperator, secondOperator);
        }
    }

    String handleOperationAsStrings(String operator1, String operator2){
        boolean containsDouble = false;
        if (operator1.contains(".") || operator2.contains(".")){
            containsDouble = true;
        }
        Double operator1Double = Double.valueOf(operator1);
        Double operator2Double = Double.valueOf(operator2);
        Double result = executeOperation(operator1Double, operator2Double);

        if (!containsDouble){
            return Integer.toString((int) executeOperation(operator1Double, operator2Double).doubleValue());
        }
        return result.toString();
    }

    abstract Double executeOperation(Double operator1, Double operator2);
}
