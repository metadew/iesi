package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class PhoneNumber extends GenerationComponentExecution {

    public PhoneNumber(GenerationDataExecution execution) {
        super(execution);
    }

    public String phoneNumber() {
        return numerify(parse(fetch("phone_number.formats")));
    }

}
