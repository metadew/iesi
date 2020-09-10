package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class App extends GenerationComponentExecution {

    public App(GenerationDataExecution execution) {
        super(execution);
    }

    public String name() {
        return fetch("app.name");
    }

    public String version() {
        return numerify(fetch("app.version"));
    }

    public String author() {
        return parse(fetch("app.author"));
    }
}