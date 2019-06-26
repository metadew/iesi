package io.metadew.iesi.datatypes;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Array extends DataType {

    private final List<DataType> list;

    public Array(List<DataType> list) {
        this.list = list;
    }

    public String toString() {
        return "{{^list(" + list.stream().map(DataType::toString).collect(Collectors.joining(", ")) + ")}}";
    }

    public List<DataType> getList() {
        return list;
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
