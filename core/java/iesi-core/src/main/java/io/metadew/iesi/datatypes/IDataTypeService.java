package io.metadew.iesi.datatypes;

import io.metadew.iesi.script.execution.ExecutionRuntime;

public interface IDataTypeService<T extends DataType> {

    public Class<T> appliesTo();
    public String keyword();
    public T resolve(String input, ExecutionRuntime executionRuntime);
    public boolean equals(T _this, T other, ExecutionRuntime executionRuntime);

}
