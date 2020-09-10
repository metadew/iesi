package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.util.List;

public class Name extends GenerationComponentExecution {

    public Name(GenerationDataExecution execution) {
        super(execution);
    }

    public String firstName() {
        return fetch("name.first_name");
    }

    public String lastName() {
        return fetch("name.last_name");
    }

    public String prefix() {
        return fetch("name.prefix");
    }

    public String suffix() {
        return fetch("name.suffix");
    }

    public String title() {
        return fetch("name.title.descriptor")
                + " " + fetch("name.title.level")
                + " " + fetch("name.title.job");
    }

    public String name() {
        return parse(fetch("name.name"));
    }

    public String nameWithMiddle() {
        return parse(fetch("name.name_with_middle"));
    }

    @SuppressWarnings("unchecked")
    public List<String> jobTitles() {
        return (List<String>) getMap("name", "title").get("job");
    }
}