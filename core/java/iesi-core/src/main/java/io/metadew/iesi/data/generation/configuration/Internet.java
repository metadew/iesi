package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Internet extends GenerationComponentExecution {

    private static final List<String> DEFAULT_SEPARATORS = Arrays.asList(".", "_");

    public Internet(GenerationDataExecution execution) {
        super(execution);
    }

    public String email() {
        return email(null);
    }

    public String email(String name) {
        return userName(name) + "@" + domainName();
    }

    public String userName(String specifier) {
        return userName(specifier, DEFAULT_SEPARATORS);
    }

    public String userName(String specifier, List<String> separators) {
        String separator = this.getGenerationTools().getRandomTools().sample(separators);

        if (specifier != null) {
            List<String> words = Arrays.asList(specifier.split("\\s"));
            List<String> normalizedWords = new ArrayList<>(words.size());
            for (String word : words) {
                normalizedWords.add(this.getGenerationTools().getStringTools().normalize(word));
            }
            return this.getGenerationTools().getStringTools().join(normalizedWords, separator);

        } else if (this.getGenerationTools().getRandomTools().randBoolean()) {
            return this.getGenerationTools().getStringTools().normalize(call("Name.first_name"))
                    + separator
                    + this.getGenerationTools().getStringTools().normalize(call("Name.last_name"));

        } else {
            return this.getGenerationTools().getStringTools().normalize(call("Name.first_name"));
        }
    }

    public String domainName() {
        return domainWord() + '.' + domainSuffix();
    }

    public String domainWord() {
        String companyName = call("Company.name");
        return this.getGenerationTools().getStringTools().normalize(companyName);
    }

    public String domainSuffix() {
        return fetch("internet.domain_suffix");
    }

}
