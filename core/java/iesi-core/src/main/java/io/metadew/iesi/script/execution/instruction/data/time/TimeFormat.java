package io.metadew.iesi.script.execution.instruction.data.time;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author robbe.berrevoets
 */
public class TimeFormat implements DataInstruction {

    private final String ORIGINAL_TIME_REPRESENTATION = "OriginalTimeRepresentation";

    private final String ORIGINAL_TIME_REPRESENTATION_FORMAT = "OriginalTimeRepresentationFormat";

    private final String DESIRED_TIME_REPRESENTATION = "DesiredTimeRepresentation";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile(
            "\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION + ">\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3})\"?\\s*" +
                    ",\\s*\"?(?<" + DESIRED_TIME_REPRESENTATION + ">[^\",]+)\"?\\s*" +
                    "(,\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION_FORMAT + ">[^\"]+)\"?)?");

    private final Pattern THREE_ARGUMENTS_PATTERN = Pattern.compile(
            "\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION + ">\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3})\"?\\s*" +
                    ",\\s*\"?(?<" + DESIRED_TIME_REPRESENTATION + ">[^\",]+)\"?\\s*" +
                    "(,\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION_FORMAT + ">[^\"]+)\"?)?");

    //date format by default
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);

        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            if (inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION_FORMAT) != null) {
                DATE_FORMAT = new SimpleDateFormat(inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION_FORMAT));

            }
            return formatTime(DATE_FORMAT,inputParameterMatcher);
        }
    }

    private String formatTime(SimpleDateFormat dateFormat,Matcher inputParameterMatcher) {
        try {
            Date originalDate = dateFormat.parse(inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION));
            SimpleDateFormat desiredDateRepresentation = new SimpleDateFormat(
                    inputParameterMatcher.group(DESIRED_TIME_REPRESENTATION));

            return desiredDateRepresentation.format(originalDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException(MessageFormat.format("Cannot generate Time from {0}",
                    inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION)));
        }
    }

    @Override
    public String getKeyword() {
        return "time.format";
    }
}
