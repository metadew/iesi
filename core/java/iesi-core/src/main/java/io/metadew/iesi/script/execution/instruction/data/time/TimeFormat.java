package io.metadew.iesi.script.execution.instruction.data.time;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author robbe.berrevoets
 */
public class TimeFormat implements DataInstruction {

    private final String ORIGINAL_TIME_REPRESENTATION_KEY = "OriginalDateRepresentation";

    private final String DESIRED_TIME_REPRESENTATION_KEY = "DesiredDateRepresentation";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile(
            "\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION_KEY + ">\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3})\"?\\s*,\\s*\"?(?<"
                    + DESIRED_TIME_REPRESENTATION_KEY + ">[^\"]+)\"?\\s*");

    private final SimpleDateFormat ORIGINAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            try {
                Date originalDate = ORIGINAL_DATE_FORMAT.parse(inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION_KEY));
                SimpleDateFormat desiredDateRepresentation = new SimpleDateFormat(
                        inputParameterMatcher.group(DESIRED_TIME_REPRESENTATION_KEY));
                return desiredDateRepresentation.format(originalDate);
            } catch (ParseException e) {
                throw new IllegalArgumentException(MessageFormat.format("Cannot generate Time from {0}",
                        inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION_KEY)));
            }
        }
    }

    @Override
    public String getKeyword() {
        return "time.format";
    }
}
