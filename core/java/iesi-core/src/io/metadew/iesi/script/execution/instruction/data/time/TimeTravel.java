package io.metadew.iesi.script.execution.instruction.data.time;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author robbe.berrevoets
 */
public class TimeTravel implements DataInstruction {
    private final String ORIGINAL_TIME_KEY = "OriginalTime";

    private final String TIME_TRAVEL_UNIT_KEY = "TimeTravelUnit";

    private final String TIME_TRAVEL_QUANTITY_KEY = "TimeTravelQuantity";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + ORIGINAL_TIME_KEY + ">\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3})\"?\\s*,\\s*\"(?<"
                    + TIME_TRAVEL_UNIT_KEY + ">\\w*)\"\\s*,\\s*(?<" + TIME_TRAVEL_QUANTITY_KEY + ">\\d+)");

    private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            try {
                Calendar originalCalendar = Calendar.getInstance();
                originalCalendar.setTime(TIME_FORMAT.parse(inputParameterMatcher.group(ORIGINAL_TIME_KEY)));
                int travelUnit = resolveTravelUnit(inputParameterMatcher.group(TIME_TRAVEL_UNIT_KEY));
                int travelQuantity = Integer.parseInt(inputParameterMatcher.group(TIME_TRAVEL_QUANTITY_KEY));
                originalCalendar.add(travelUnit, travelQuantity);
                return TIME_FORMAT.format(originalCalendar.getTime());
            } catch (ParseException e) {
                throw new IllegalArgumentException(
                        MessageFormat.format("Cannot generate Time from {0}", inputParameterMatcher.group(ORIGINAL_TIME_KEY)));
            }

        }
    }

    private int resolveTravelUnit(String representation) {
        switch (representation.toLowerCase()) {
            case "hour":
                return Calendar.HOUR;
            case "minute":
                return Calendar.MINUTE;
            case "second":
                return Calendar.SECOND;
            default:
                throw new IllegalArgumentException(MessageFormat.format("Time travel does not work with unit {0}", representation));
        }
    }

    @Override
    public String getKeyword() {
        return "time.travel";
    }
}
