package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class University extends GenerationComponentExecution {

    public University(GenerationDataExecution execution) {
        super(execution);
    }

    public String name() {
        return parse(fetch("university.name"));
    }

    public String prefix() {
        return fetch("university.prefix");
    }

    public String suffix() {
        return fetch("university.suffix");
    }
}
