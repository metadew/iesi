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

    private final String DESIRED_TIME_REPRESENTATION_FORMAT = "DesiredTimeRepresentationFormat";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile(
            "\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION + ">\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3})\"?\\s*" +
                    ",\\s*\"?(?<" + DESIRED_TIME_REPRESENTATION_FORMAT + ">[^\",]+)\"?\\s*" +
                    "(,\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION_FORMAT + ">[^\"]+)\"?)?");

    private final SimpleDateFormat ORIGINAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);

        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            if (inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION_FORMAT) != null) {
                try {
                    Date originalDate = ORIGINAL_DATE_FORMAT.parse(inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION));
                    SimpleDateFormat desiredDateRepresentation = new SimpleDateFormat(
                            inputParameterMatcher.group(DESIRED_TIME_REPRESENTATION_FORMAT));
                    return desiredDateRepresentation.format(originalDate);
                } catch (ParseException e) {
                    throw new IllegalArgumentException(MessageFormat.format("Cannot generate Time from {0}",
                            inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION)));
                }
            } else {
                System.out.println("ICI C 2 ARGUMENTS");
                try {
                    Date originalDate = ORIGINAL_DATE_FORMAT.parse(inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION));
                    SimpleDateFormat desiredDateRepresentation = new SimpleDateFormat(
                            inputParameterMatcher.group(DESIRED_TIME_REPRESENTATION_FORMAT));
                    return desiredDateRepresentation.format(originalDate);
                } catch (ParseException e) {
                    throw new IllegalArgumentException(MessageFormat.format("Cannot generate Time from {0}",
                            inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION)));
                }
            }





        }
    }
/*
    private String formatTime(String originalTimeRepresentation, String desiredTimeRepresentation,Matcher inputParameterMatcher) {

                SimpleDateFormat desiredDateRepresentation = new SimpleDateFormat(
                        inputParameterMatcher.group(DESIRED_TIME_REPRESENTATION_FORMAT));
                return desiredDateRepresentation.format(originalDate);
        //return LocalDate.parse(originalDate, ORIGINAL_DATE_FORMAT).format(DateTimeFormatter.ofPattern(targetedFormat));
    }
    private String formatTime(String originalTimeRepresentation, String originalTimeRepresentationFormat, String desiredTimeRepresentation,Matcher inputParameterMatcher) {
        return LocalDate.parse(originalDate, DateTimeFormatter.ofPattern(originalFormat)).format(DateTimeFormatter.ofPattern(targetedFormat));
    }
*/
    @Override
    public String getKeyword() {
        return "time.format";
    }
}
