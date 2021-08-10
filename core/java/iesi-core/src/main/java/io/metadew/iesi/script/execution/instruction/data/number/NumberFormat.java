package io.metadew.iesi.script.execution.instruction.data.number;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberFormat implements DataInstruction {

    private final static String NUMBER = "number";
    private final static String FORMAT = "format";

    private final static Pattern PATTERN = Pattern.compile("\\s*\"(?<" + NUMBER + ">.+)\"\\s*,\\s*\"(?<" + FORMAT + ">.+)\"");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameter = PATTERN.matcher(parameters);
        if (inputParameter.find()) {
            try {
                long number = Long.parseLong(inputParameter.group(NUMBER));
                String format = inputParameter.group(FORMAT);
                String output = String.format(format, number);
                return output;
            } catch (NumberFormatException e) {
                throw new NumberFormatException("String cannot be converted to number");
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), parameters));
        }
    }

    @Override
    public String getKeyword() {
        return "number.format";
    }

}
