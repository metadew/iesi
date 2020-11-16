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

    private final static String ORIGINAL_TIME_REPRESENTATION = "OriginalTimeRepresentation";

    private final static String ORIGINAL_TIME_REPRESENTATION_FORMAT = "OriginalTimeRepresentationFormat";

    private final static String DESIRED_TIME_REPRESENTATION = "DesiredTimeRepresentation";

    private final static Pattern THREE_ARGUMENTS_PATTERN = Pattern.compile(
                    "\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION + ">[^\"]+)\"?\\s*" +
                    ",\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION_FORMAT + ">[^\"]+)\"?\\s*"+
                    ",\\s*\"?(?<" +  DESIRED_TIME_REPRESENTATION + ">[^\"]+)\"?");

    private final static Pattern TWO_ARGUMENTS_PATTERN = Pattern.compile(
                    "\\s*\"?(?<" + ORIGINAL_TIME_REPRESENTATION + ">[^\"]+)\"?\\s*" +
                    ",\\s*\"?(?<" +  DESIRED_TIME_REPRESENTATION + ">[^\"]+)\"?");


    //date format by default
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String generateOutput(String parameters) {
        Matcher threeParameterMatcher = THREE_ARGUMENTS_PATTERN.matcher(parameters);
        Matcher twoParameterMatcher = TWO_ARGUMENTS_PATTERN.matcher(parameters);

        if(threeParameterMatcher.find()){

            SimpleDateFormat  dateFormatCustom = new SimpleDateFormat(threeParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION_FORMAT));
            return formatTime(dateFormatCustom,threeParameterMatcher);

        } else if (twoParameterMatcher.find()){
            return formatTime(DATE_FORMAT,twoParameterMatcher);

        } else {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        }

    }

    private String formatTime(SimpleDateFormat dateFormat,Matcher inputParameterMatcher) {
        try {
            Date dateFormatTargeted = dateFormat.parse(inputParameterMatcher.group(ORIGINAL_TIME_REPRESENTATION));
            SimpleDateFormat desiredDateRepresentation = new SimpleDateFormat(
                    inputParameterMatcher.group(DESIRED_TIME_REPRESENTATION));

            return desiredDateRepresentation.format(dateFormatTargeted);
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
