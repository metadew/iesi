package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

public class PhoneNumber extends GenerationComponentExecution {

    private static final int DEFAULT_EXTENSION_LENGHT = 4;

    public PhoneNumber(GenerationDataExecution execution) {
        super(execution);
    }

    public String phoneNumber() {
        return numerify(parse(fetch("phone_number.formats")));
    }

    public String cellPhone() {
        return numerify(parse(fetch("cell_phone.formats")));
    }

    // US only
    public String areaCode() {
        return fetch("phone_number.area_code");
    }

    // US only
    public String exchangeCode() {
        return fetch("phone_number.exchange_code");
    }

    // US only
    public String subscriberNumber() {
        return subscriberNumber(DEFAULT_EXTENSION_LENGHT);
    }

    // US only
    public String subscriberNumber(int length) {
        return String.format("%0" + length + "d", this.getGenerationTools().getRandomTools().numberByLength(length));
    }

    // US only
    public String extension() {
        return subscriberNumber();
    }

    // US only
    public String extension(int length) {
        return subscriberNumber(length);
    }
}
