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
    private final String ORIGINAL_DATE_REPRESENTATION_KEY = "OriginalDateRepresentation";

    private final String DESIRED_DATE_REPRESENTATION_KEY = "DesiredDateRepresentation";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile("\\s*\"?(?<" + ORIGINAL_DATE_REPRESENTATION_KEY
            + ">\\d{8})\"?\\s*,\\s*\"?(?<" + DESIRED_DATE_REPRESENTATION_KEY + ">[^\"]+)\"?\\s*");

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
            LocalDate date = LocalDate.parse(inputParameterMatcher.group(ORIGINAL_DATE_REPRESENTATION_KEY), ORIGINAL_DATE_FORMAT);
            return date.format(DateTimeFormatter.ofPattern(inputParameterMatcher.group(DESIRED_DATE_REPRESENTATION_KEY)));
        }
    }

    @Override
    public String getKeyword() {
        return "date.format";
    }
}
