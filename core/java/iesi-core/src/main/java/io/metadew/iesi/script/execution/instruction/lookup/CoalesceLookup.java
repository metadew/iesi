package io.metadew.iesi.script.execution.instruction.lookup;

import java.util.Arrays;
import java.util.Optional;

public class CoalesceLookup implements LookupInstruction {

    private final String DEFAULT = "";

    @Override
    public String getKeyword() {
        return "coalesce";
    }

    @Override
    public String generateOutput(String parameters) {

        Optional<String> hit = Arrays.stream(parameters.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .findFirst();
        return hit.orElse(DEFAULT);
    }
}
