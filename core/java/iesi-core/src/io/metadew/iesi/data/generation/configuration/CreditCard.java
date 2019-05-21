package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.time.LocalDate;

public class CreditCard extends GenerationComponentExecution {

    private static final int CREDIT_CARD_PLUS_YEARS_MAX = 4;

    private final Date date;

    public CreditCard(GenerationDataExecution execution) {
        super(execution);
        this.date = new Date(execution);
    }

    public String creditCardNumber() {
        return fetch("creditcard.credit_card_numbers");
    }

    public LocalDate creditCardExpireDate() {
        return date.today().plusYears(this.getGenerationTools().getRandomTools().number(CREDIT_CARD_PLUS_YEARS_MAX) + 1);
    }

    public String creditCardType() {
        return fetch("creditcard.credit_card_types");
    }
}