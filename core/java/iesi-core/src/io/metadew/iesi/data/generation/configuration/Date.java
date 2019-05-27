package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Date extends GenerationComponentExecution {

    private static final int DEFAULT_NUM_OF_DAYS = 365;

    private static final int DEFAULT_MIN_AGE = 18;

    private static final int DEFAULT_MAX_AGE = 65;

    private final DateTimeFormatter dateFormat;

    public Date(GenerationDataExecution generationDataExecution) {
        super(generationDataExecution);
        dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public LocalDate between(String from, String to, DateTimeFormatter dateFormat) {
        LocalDate fromLocalDate = LocalDate.parse(from, dateFormat);
        LocalDate toLocalDate = LocalDate.parse(from, dateFormat);
        return between(fromLocalDate, toLocalDate);
    }

    public LocalDate between(String from, String to) {
        return between(from, to, dateFormat);
    }

    public LocalDate forward() {
        return forward(DEFAULT_NUM_OF_DAYS);
    }

    public LocalDate forward(int numberOfDays) {
        return between(LocalDate.now(), LocalDate.now().plusDays(numberOfDays));
    }

    public LocalDate backward() {
        return backward(DEFAULT_NUM_OF_DAYS);
    }

    public LocalDate backward(int numberOfDays) {
        return between(LocalDate.now().minusDays(numberOfDays), LocalDate.now());
    }

    public LocalDate birthday() {
        return birthday(DEFAULT_MIN_AGE, DEFAULT_MAX_AGE);
    }

    public LocalDate birthday(int minAge, int maxAge) {
        return between(LocalDate.now().minusYears(maxAge), LocalDate.now().minusYears(minAge));
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
