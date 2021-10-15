package io.metadew.iesi.datatypes;

import io.metadew.iesi.script.execution.ExecutionRuntime;

public interface IDataTypeService<T extends DataType> {

    Class<T> appliesTo();
    String keyword();
    T resolve(String input, ExecutionRuntime executionRuntime);
    boolean equals(T _this, T other, ExecutionRuntime executionRuntime);

}
