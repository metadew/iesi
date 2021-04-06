package io.metadew.iesi.script.execution.instruction.data.date;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.instruction.data.DataInstruction;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author Suyash Jain
 */
public class ListSize implements DataInstruction {

    private final ExecutionRuntime executionRuntime;

    public ListSize(ExecutionRuntime executionRuntime) {
        this.executionRuntime = executionRuntime;
    }

    @Override
    public String getKeyword() {
        return "list.size";
    }

    @Override
    public String generateOutput(String parameters) {
        Array array = getArray(DataTypeHandler.getInstance().resolve(parameters, executionRuntime));
        List<DataType> list = array.getList();
        return Integer.toString(list.size());
    }

    private Array getArray(DataType array) {
        if (array instanceof Array) {
            return (Array) array;
        } else if (array instanceof Text) {
            return executionRuntime.getArray(((Text) array).getString())
                    .orElseThrow(() -> new IllegalArgumentException(MessageFormat.format("No array found", ((Text) array).getString())));
        } else {
            throw new IllegalArgumentException(MessageFormat.format("list cannot be of type {0}", array.getClass()));
        }
    }
}
