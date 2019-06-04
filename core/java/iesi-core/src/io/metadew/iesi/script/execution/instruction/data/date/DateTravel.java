package io.metadew.iesi.script.execution.instruction.data.date;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author robbe.berrevoets
 */
public class DateTravel implements DataInstruction {
    private final String ORIGINAL_DATE_KEY = "OriginalDate";

    private final String DATE_TRAVEL_UNIT_KEY = "DateTravelUnit";

    private final String DATE_TRAVEL_QUANTITY_KEY = "DateTravelQuantity";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile("\\s*\"?(?<" + ORIGINAL_DATE_KEY + ">\\d{8})\"?\\s*,\\s*\"(?<"
            + DATE_TRAVEL_UNIT_KEY + ">\\w*)\"\\s*,\\s*(?<" + DATE_TRAVEL_QUANTITY_KEY + ">\\-?\\d+)");

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            LocalDate startDate = LocalDate.parse(inputParameterMatcher.group(ORIGINAL_DATE_KEY), DATE_FORMAT);
            ChronoUnit chronoUnit = resolveTravelUnit(inputParameterMatcher.group(DATE_TRAVEL_UNIT_KEY));
            LocalDate travelledDate = travel(startDate, Integer.parseInt(inputParameterMatcher.group(DATE_TRAVEL_QUANTITY_KEY)),chronoUnit);
            return travelledDate.format(DATE_FORMAT);
        }
    }

    private LocalDate travel(LocalDate startDate, int quantity, ChronoUnit chronoUnit) {
        return startDate.plus(quantity, chronoUnit);
    }

    private ChronoUnit resolveTravelUnit(String representation) {
        switch (representation.toLowerCase()) {
            case "year":
                return ChronoUnit.YEARS;
            case "month":
                return ChronoUnit.MONTHS;
            case "day":
                return ChronoUnit.DAYS;
            default:
                throw new IllegalArgumentException(MessageFormat.format("Date travel does not work with unit {0}", representation));
        }
    }

    @Override
    public String getKeyword() {
        return "date.travel";
    }

}
