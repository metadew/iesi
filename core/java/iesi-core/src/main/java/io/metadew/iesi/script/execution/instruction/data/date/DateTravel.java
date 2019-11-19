package io.metadew.iesi.script.execution.instruction.data.date;

import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author robbe.berrevoets
 */
public class DateTravel implements DataInstruction {
    private final String ORIGINAL_DATE_KEY = "OriginalDate";

    private final String DATE_TRAVEL_UNIT_KEY = "DateTravelUnit";

    private final String DATE_TRAVEL_QUANTITY_KEY = "DateTravelQuantity";

    private final String WORKDAY_FLAG_KEY ="WorkdayFlag";

    private final String Regex = "[nN]?[wW]";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern.compile("\\s*\"?(?<" + ORIGINAL_DATE_KEY + ">\\d{8})\"?\\s*,\\s*\"(?<"
            + DATE_TRAVEL_UNIT_KEY + ">\\w*)\"\\s*,\\s*(?<" + DATE_TRAVEL_QUANTITY_KEY + ">\\-?\\d+)" +
            "(\\s*,\\s*(?<" + WORKDAY_FLAG_KEY + ">([nN]?[wW])))?");

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);

        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            LocalDate startDate = LocalDate.parse(inputParameterMatcher.group(ORIGINAL_DATE_KEY), DATE_FORMAT);
            ChronoUnit chronoUnit = resolveTravelUnit(inputParameterMatcher.group(DATE_TRAVEL_UNIT_KEY));
            int travelQuantity = Integer.parseInt(inputParameterMatcher.group(DATE_TRAVEL_QUANTITY_KEY));
            String workdayCondition =  inputParameterMatcher.group(WORKDAY_FLAG_KEY);
            LocalDate travelledDate = workdayCondition==null ? travel(startDate, travelQuantity,chronoUnit) :
                    travel(startDate, travelQuantity,chronoUnit, workdayCondition);

            return travelledDate.format(DATE_FORMAT);
        }
    }

    private LocalDate travel(LocalDate startDate, int quantity, ChronoUnit chronoUnit) {
        return startDate.plus(quantity, chronoUnit);
    }

    private LocalDate travel(LocalDate startDate, int quantity, ChronoUnit chronoUnit, String workdayFlag) {
        int loopAmount = quantity/Math.abs(quantity);
        LocalDate endDateNotChecked = startDate.plus(quantity, chronoUnit);
        // always return true so when there is no condition given, then there won't be one checked
        Predicate<DayOfWeek> condition;

        switch (workdayFlag.toLowerCase()){
            case "w":
                condition = (day) -> !isWeekendDay(day);
                break;
            case "nw":
                condition = this::isWeekendDay;
                break;
            default:
                throw new IllegalArgumentException(MessageFormat.format("Date travel does not work with condition {0}", workdayFlag));
        }

        return findNextDay(endDateNotChecked, loopAmount, condition);
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

    private LocalDate findNextDay(LocalDate currentDate, int loopAmount, Predicate<DayOfWeek> condition){
        while (condition!=null && !condition.test(currentDate.getDayOfWeek())){
            currentDate = currentDate.plus(loopAmount, ChronoUnit.DAYS);
        }
        return currentDate;
    }

    private Boolean isWeekendDay(DayOfWeek day){
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    @Override
    public String getKeyword() {
        return "date.travel";
    }

}
