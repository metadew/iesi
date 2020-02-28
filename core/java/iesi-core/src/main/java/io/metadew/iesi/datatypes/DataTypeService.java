package io.metadew.iesi.datatypes;

import io.metadew.iesi.script.execution.ExecutionRuntime;

public interface DataTypeService<T extends DataType> {

    public Class<T> appliesTo();
    public String keyword();
    public T resolve(String input, ExecutionRuntime executionRuntime);

}
