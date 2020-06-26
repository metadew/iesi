package io.metadew.iesi.datatypes.dataset;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public abstract class DatasetService<T extends Dataset> implements IDatasetService<T> {

    @Override
    public boolean equals(T _this, T other, ExecutionRuntime executionRuntime) {
        if (!_this.getClass().equals(other.getClass())) {
            return false;
        }
        Map<String, DataType> thisDataItems = getDataItems(_this, executionRuntime);
        Map<String, DataType> otherDataItems = getDataItems(_this, executionRuntime);
        if (!thisDataItems.keySet().equals(otherDataItems.keySet())) {
            return false;
        }
        for (Map.Entry<String, DataType> thisDataItem : thisDataItems.entrySet()) {
            if(!DatasetHandler.getInstance().getDataItem(other, thisDataItem.getKey(), executionRuntime)
                    .map(dataType -> DataTypeHandler.getInstance().equals(dataType, thisDataItem.getValue(), executionRuntime))
                    .orElse(false)) {
                return false;
            }
        }
        return true;
    }
}
