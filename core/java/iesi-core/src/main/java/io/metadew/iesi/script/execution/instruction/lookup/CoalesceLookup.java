package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.springframework.expression.EvaluationException;

import java.util.Arrays;
import java.util.Optional;

public class CoalesceLookup implements LookupInstruction {

    private static final String DEFAULT = "";
    private final ExecutionRuntime executionRuntime;

    public CoalesceLookup(ExecutionRuntime executionRuntime) {
        this.executionRuntime = executionRuntime;
    }

    @Override
    public String getKeyword() {
        return "coalesce";
    }

    @Override
    public String generateOutput(String parameters) {
        Optional<String> hit = Arrays.stream(parameters.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .filter(value -> !(DataTypeHandler.getInstance().resolve(value, executionRuntime) instanceof Null))
                .findFirst();
        return hit.orElse(DEFAULT);
    }
}
