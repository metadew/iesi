package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;
import io.metadew.iesi.data.generation.tools.PeriodTools;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Time extends GenerationComponentExecution {

    private final Date date;

    public Time(GenerationDataExecution execution) {
        super(execution);
        this.date = new Date(execution);
    }

    public LocalDateTime between(LocalDateTime from, LocalDateTime to) {
        return between(from.toLocalDate(), to.toLocalDate(), PeriodTools.all);
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    private LocalDateTime between(LocalDate from, LocalDate to, PeriodTools period) {
        return LocalDateTime.of(date.between(from, to), randomTime(period));
    }

    private LocalTime randomTime(PeriodTools period) {
        return LocalTime.of(hours(period), minutes(), seconds());
    }

    private int hours(PeriodTools period) {
        int[] values = period.getValues();
        return values[this.getGenerationTools().getRandomTools().number(values.length)];
    }

    private int minutes() {
        return this.getGenerationTools().getRandomTools().number(60);
    }

    private int seconds() {
        return this.getGenerationTools().getRandomTools().number(60);
    }
}
