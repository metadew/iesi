package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.time.LocalDateTime;

public class Time extends GenerationComponentExecution {

    public Time(GenerationDataExecution execution) {
        super(execution);
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
