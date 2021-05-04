package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.mozilla.javascript.EvaluatorException;
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
                .map(value -> DataTypeHandler.getInstance().resolve(value, executionRuntime))
                .filter(value -> !(value instanceof Null))
                .map(value -> {
                    if (value instanceof Text) {
                        return ((Text) value).getString();
                    } else {
                        return value.toString();
                    }
                })
                .findFirst();
        return hit.orElse(DEFAULT);
    }
}
