package io.metadew.iesi.script.execution.instruction.data.number;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import lombok.extern.log4j.Log4j2;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class NumberFormat implements DataInstruction {

    private static final String NUMBER = "number";
    private static final String FORMAT = "format";

    private static final Pattern PATTERN = Pattern.compile("\\s*\"?(?<" + NUMBER + ">.+?)\"?\\s*,\\s*\"(?<" + FORMAT + ">.+)\"");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameter = PATTERN.matcher(parameters);
        if (inputParameter.find()) {
            try {
                Double number = Double.parseDouble(inputParameter.group(NUMBER));
                DecimalFormat format = new DecimalFormat(inputParameter.group(FORMAT).replaceAll("\\*", "#"));
                format.setRoundingMode(RoundingMode.FLOOR);
                return format.format(number);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("String cannot be converted to decimal");
            }
        } else {
            throw new IllegalArgumentException(String.format("Illegal arguments provided to %s:%s", this.getKeyword(), parameters));
        }
    }

    @Override
    public String getKeyword() {
        return "number.format";
    }

}
