package io.metadew.iesi.script.execution.instruction.data.number;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author peter.billen
 */
public class NumberBetween implements DataInstruction {
    private final String LOWER_BOUND_KEY = "LowerBoundRepresentation";

    private final String UPPER_BOUND_KEY = "UpperBoundRepresentation";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + LOWER_BOUND_KEY + ">\\d+)\"?\\s*,\\s*\"?(?<" + UPPER_BOUND_KEY + ">\\d+)\"?\\s*");

    private final GenerationObjectExecution generationObjectExecution;

    public NumberBetween(GenerationObjectExecution generationObjectExecution) {
        this.generationObjectExecution = generationObjectExecution;
    }

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            Double lowerBound = Double.parseDouble(inputParameterMatcher.group(LOWER_BOUND_KEY));
            Double upperBound = Double.parseDouble(inputParameterMatcher.group(UPPER_BOUND_KEY));
            return Double.toString(generationObjectExecution.getNumber().between(lowerBound, upperBound));
        }
    }

    @Override
    public String getKeyword() {
        return "number.between";
    }
}
