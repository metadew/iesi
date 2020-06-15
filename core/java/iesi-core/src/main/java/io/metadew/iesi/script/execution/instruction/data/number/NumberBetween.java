package io.metadew.iesi.script.execution.instruction.data.number;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberBetween implements DataInstruction {
    private final String LOWER_BOUND_KEY = "LowerBoundRepresentation";
    private final String UPPER_BOUND_KEY = "UpperBoundRepresentation";
    private final String NUMBER_OF_DECIMALS = "NumberOfDecimalRepresentation";
    private final SecureRandom secureRandom;

    public NumberBetween() {
        secureRandom = new SecureRandom();
    }

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("^\\s*\"?\\s*(?<" + LOWER_BOUND_KEY + ">-?\\d+(?:\\.\\d+)?)\\s*\"?\\s*," +
                    "\\s*\"?\\s*(?<" + UPPER_BOUND_KEY + ">-?\\d+(?:\\.\\d+)?)\\s*\"?\\s*" +
                    "(?:,\\s*\"?\\s*(?<" + NUMBER_OF_DECIMALS + ">-?\\d*(?:\\.\\d+)?)\\s*\"?\\s*)?");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);

        if (!inputParameterMatcher.find())
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));

        String strNumberOfDecimals = inputParameterMatcher.group(NUMBER_OF_DECIMALS);

        double lowerBound = Double.parseDouble(inputParameterMatcher.group(LOWER_BOUND_KEY));
        double upperBound = Double.parseDouble(inputParameterMatcher.group(UPPER_BOUND_KEY));
        double generatedNumber = range(lowerBound, upperBound);

        if (strNumberOfDecimals == null)
            return Double.toString(generatedNumber);

        if (Pattern.compile("\\.").matcher(strNumberOfDecimals).find())
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));

        int numberOfDecimals = Integer.parseInt(strNumberOfDecimals);

        if (numberOfDecimals < 0)
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));

        return getADecimalFormatter(numberOfDecimals)
                .format(generatedNumber);

    }

    @Override
    public String getKeyword() {
        return "number.between";
    }

    /**
     * @param numberOfDecimal int, it is the number of decimal the formatter will be configured for
     * @return an instance of DecimalFormat configured with RoundingMode.HALF_EVEN and groupUse to false
     */
    private DecimalFormat getADecimalFormatter(int numberOfDecimal) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormatter = new DecimalFormat("#", decimalFormatSymbols);
        decimalFormatter.setMinimumIntegerDigits(1);
        decimalFormatter.setMaximumFractionDigits(numberOfDecimal);
        decimalFormatter.setMinimumFractionDigits(numberOfDecimal);
        decimalFormatter.setRoundingMode(RoundingMode.HALF_EVEN);
        decimalFormatter.setGroupingUsed(false);
        return decimalFormatter;
    }


    /**
     * @param min double
     * @param max double
     * @return a secure random number between min and max parameter
     */
    private double range(double min, double max) {
        if (max < min) {
            double temp = max;
            max = min;
            min = temp;
        }
        return secureRandom.nextDouble() * (max - min) + min;
    }
}
