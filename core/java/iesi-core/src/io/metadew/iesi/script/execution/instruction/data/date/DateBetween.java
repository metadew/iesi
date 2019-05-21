package io.metadew.iesi.script.execution.instruction.data.date;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author robbe.berrevoets
 */
public class DateBetween implements DataInstruction {
    private final String BEGIN_DATE_KEY = "OriginalDateRepresentation";

    private final String END_DATE_KEY = "DesiredDateRepresentation";

    private final Pattern INPUT_PARAMETER_PATTERN = Pattern
            .compile("\\s*\"?(?<" + BEGIN_DATE_KEY + ">\\d{8})\"?\\s*,\\s*\"?(?<" + END_DATE_KEY + ">\\d{8})\"?\\s*");

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    private final GenerationObjectExecution generationObjectExecution;

    public DateBetween(GenerationObjectExecution generationObjectExecution) {
        this.generationObjectExecution = generationObjectExecution;
    }

    @Override
    public String generateOutput(String parameters) {
        Matcher inputParameterMatcher = INPUT_PARAMETER_PATTERN.matcher(parameters);
        if (!inputParameterMatcher.find()) {
            throw new IllegalArgumentException(MessageFormat.format("Illegal arguments provided to " + this.getKeyword() + ": {0}", parameters));
        } else {
            LocalDate beginDate = LocalDate.parse(inputParameterMatcher.group(BEGIN_DATE_KEY), DATE_FORMAT);
            LocalDate endDate = LocalDate.parse(inputParameterMatcher.group(END_DATE_KEY), DATE_FORMAT);
            return generationObjectExecution.getDate().between(beginDate, endDate).format(DATE_FORMAT);
        }
    }

    @Override
    public String getKeyword() {
        return "date.between";
    }
}
