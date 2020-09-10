package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.util.List;


public class Company extends GenerationComponentExecution {

    public Company(GenerationDataExecution execution) {
        super(execution);
    }

    public String name() {
        return parse(fetch("company.name"));
    }

    public String suffix() {
        return fetch("company.suffix");
    }

    public String industry() {
        return fetch("company.industry");
    }

    @SuppressWarnings("rawtypes")
    public String catchPhrase() {
        List buzzwordsSections = getList("company", "buzzwords");
        String catchPhrase = "";
        for (Object buzzwordsSection : buzzwordsSections) {
            catchPhrase += sampleFromList((List) buzzwordsSection) + " ";
        }
        return catchPhrase.substring(0, catchPhrase.length() - 1);
    }

    public String buzzwords() {
        return fetch("company.buzzwords");
    }

    public String bs() {
        return fetch("company.bs");
    }

    public String logo() {
        int randomNum = this.getGenerationTools().getRandomTools().range(1, 12);
        return "https://url/" + randomNum + ".png";
    }

    public String profession() {
        return fetch("company.profession");
    }
}
