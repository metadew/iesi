package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Date extends GenerationComponentExecution {

    public Date(GenerationDataExecution generationDataExecution) {
        super(generationDataExecution);
    }

    public LocalDate between(LocalDate from, LocalDate to) {
        long daysBetween = from.until(to, ChronoUnit.DAYS);
        int randomDaysToAdd;
        if (daysBetween > 0) {
            randomDaysToAdd = (int) this.getGenerationTools().getRandomTools().number(daysBetween + 1);
            return from.plusDays(randomDaysToAdd);
        } else if (daysBetween < 0) {
            randomDaysToAdd = (int) (daysBetween + this.getGenerationTools().getRandomTools().number(Math.abs(daysBetween) + 1));
            return from.plusDays(randomDaysToAdd);
        } else {
            return from;
        }
    }

    public LocalDate today() {
        return LocalDate.now();
    }

}
