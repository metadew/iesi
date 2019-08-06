package io.metadew.iesi.datatypes.array;

import io.metadew.iesi.datatypes.DataType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Array extends DataType {

    private final List<DataType> list;

    public Array(List<DataType> list) {
        this.list = list;
    }
    public Array() {
        this.list = new ArrayList<>();
    }

    public String toString() {
        return "{{^list(" + list.stream().map(DataType::toString).collect(Collectors.joining(", ")) + ")}}";
    }

    public List<DataType> getList() {
        return list;
    }

    public void add(DataType element) {
        list.add(element);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Array) {
            return CollectionUtils.isEqualCollection(this.list, ((Array) obj).getList());
        } else {
            return false;
        }
    }
}
