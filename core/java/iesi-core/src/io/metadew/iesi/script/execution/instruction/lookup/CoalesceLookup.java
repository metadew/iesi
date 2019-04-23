package io.metadew.iesi.script.execution.instruction.lookup;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

public class CoalesceLookup implements LookupInstruction {
    @Override
    public String getKeyword() {
        return "coalesce";
    }

    @Override
    public String generateOutput(String parameters) {
        Optional<String> hit = Arrays.stream(parameters.split(","))
                .filter(value -> !value.trim().isEmpty())
                .findFirst();

        if (!hit.isPresent()) {
            throw new IllegalArgumentException(MessageFormat.format("No hit found for coalesce of {0}", parameters));
        } else {
            return hit.get();
        }
    }
}
