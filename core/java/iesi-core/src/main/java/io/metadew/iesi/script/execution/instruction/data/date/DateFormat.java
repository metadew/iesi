package io.metadew.iesi.script.execution.instruction.data.date;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author robbe.berrevoets
 */
public class DateFormat implements DataInstruction {
    private final String FIRST_PARAMETER = "FirstParameter";
    private final String SECOND_PARAMETER = "SecondParameter";
    private final String THIRD_PARAMETER = "ThirdParameter";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile("\\s*\"?(?<"+FIRST_PARAMETER+">[\\d-\\w\\./]+)\"?\\s*,\\s*\"?(?<"+SECOND_PARAMETER+">[^\",]+)\"?\\s*(,\\s*\"?(?<"+THIRD_PARAMETER+">[^\"]+)\"?)?");

//            "\\s*\"?(?<" + FIRST_PARAMETER
//            + ">\\d{8})\"?\\s*,\\s*\"?(?<" + SECOND_PARAMETER + ">[^\"]+)\"?\\s*");

    private final DateTimeFormatter ORIGINAL_DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    /**
     * @param parameters
     * @return transform the given date (in format ddMMyyyy) to the format desired
     */
    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            if (inputParameterMatcher.group(THIRD_PARAMETER) != null) {
                return formatDate(inputParameterMatcher.group(FIRST_PARAMETER), inputParameterMatcher.group(SECOND_PARAMETER), inputParameterMatcher.group(THIRD_PARAMETER));
            } else {
                return formatDate(inputParameterMatcher.group(FIRST_PARAMETER), inputParameterMatcher.group(SECOND_PARAMETER));
            }
        }
    }

    private String formatDate(String originalDate, String targetedFormat) {
        return LocalDate.parse(originalDate, ORIGINAL_DATE_FORMAT).format(DateTimeFormatter.ofPattern(targetedFormat));
    }

    private String formatDate(String originalDate, String originalFormat, String targetedFormat) {
        return LocalDate.parse(originalDate, DateTimeFormatter.ofPattern(originalFormat)).format(DateTimeFormatter.ofPattern(targetedFormat));
    }

    @Override
    public String getKeyword() {
        return "date.format";
    }
}
