package io.metadew.iesi.script.execution.instruction.data.date;

import io.metadew.iesi.data.generation.execution.GenerationObjectExecution;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;

import java.time.format.DateTimeFormatter;

/**
 * @author robbe.berrevoets
 */
public class DateToday implements DataInstruction {
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    private final GenerationObjectExecution generationObjectExecution;

    public DateToday(GenerationObjectExecution generationObjectExecution) {
        this.generationObjectExecution = generationObjectExecution;
    }

    /**
     * @return the current date in format ddMMyyyy
     */
    @Override
    public String generateOutput(String parameters) {
        return generationObjectExecution.getDate().today().format(DATE_FORMAT);
    }

    @Override
    public String getKeyword() {
        return "date.today";
    }
}
