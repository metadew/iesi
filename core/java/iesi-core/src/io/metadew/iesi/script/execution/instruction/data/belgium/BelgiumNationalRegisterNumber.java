package io.metadew.iesi.script.execution.instruction.data.belgium;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author robbe.berrevoets
 */
public class BelgiumNationalRegisterNumber implements DataInstruction {
    private final String BIRTHDAY_KEY = "Birthday";

    private final String SEX_KEY = "Sex";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + BIRTHDAY_KEY + ">\\d{8})\"?\\s*,\\s*(?<" + SEX_KEY + ">\\d)\\s*");

    private final String YEAR_KEY = "Year";

    private final String MONTH_KEY = "Month";

    private final String DAY_KEY = "Day";

    private final Pattern BIRTHDAY_PATTERN = Pattern
            .compile("(?<" + DAY_KEY + ">\\d{2})(?<" + MONTH_KEY + ">\\d{2})(?<" + YEAR_KEY + ">\\d{4})");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to date format: {0}", parameters));
        }
        String birthdayDate = inputParameterMatcher.group(BIRTHDAY_KEY);
        String sex = inputParameterMatcher.group(SEX_KEY);

        Matcher dateMatcher = BIRTHDAY_PATTERN.matcher(birthdayDate);
        if (!dateMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat
                    .format("Illegal arguments provided to national registernumber for birhtday date parameter: {0}", birthdayDate));
        }

        // National Register Number: Rules based on birth date
        long yearRepresentation = Long.parseLong(dateMatcher.group(YEAR_KEY).substring(2)) * (long) Math.pow(10, 7);
        long monthRepresentation = Long.parseLong(dateMatcher.group(MONTH_KEY)) * (long) Math.pow(10, 5);
        long dayRepresentation = Long.parseLong(dateMatcher.group(DAY_KEY)) * (long) Math.pow(10, 3);

        // National Register Number: Rules based on sex
        Random randomGenerator = new Random();
        long maleFemaleRepresentation;
        if (sex.equalsIgnoreCase("1")) {
            // Male
            maleFemaleRepresentation = 1 + randomGenerator.nextInt(498) * 2;
        } else if (sex.equalsIgnoreCase("2")) {
            // Female
            maleFemaleRepresentation = 2 + randomGenerator.nextInt(498) * 2;
        } else {
            throw new IllegalArgumentException(
                    MessageFormat.format("Illegal arguments provided to national registernumber for sex parameter: {0}", sex));
        }
        BigInteger baseNumber = BigInteger.valueOf(yearRepresentation).add(BigInteger.valueOf(monthRepresentation))
                .add(BigInteger.valueOf(dayRepresentation)).add(BigInteger.valueOf(maleFemaleRepresentation));

        // National Register Number: Rules based on birth date (2)
        BigInteger number = baseNumber;
        if (Integer.parseInt(dateMatcher.group(YEAR_KEY)) >= 2000) {
            number = number.add(BigInteger.valueOf(2 * (long) Math.pow(10, 9)));
        }
        // National Register Number: control number
        BigInteger modulus = BigInteger.valueOf(97);
        BigInteger rest = number.mod(modulus);
        baseNumber = baseNumber.multiply(BigInteger.valueOf((long) Math.pow(10, 2))).add(modulus.subtract(rest));

        String output = baseNumber.toString();

        // Clean up through leading zero formatting
        if (output.length() < 11) {
            while (output.length() < 11) {
                output = "0" + output;
            }
        }

        return output;
    }

    @Override
    public String getKeyword() {
        return "belgium.nationalregisternumber";
    }
}
