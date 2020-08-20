package io.metadew.iesi.datatypes.array;

import io.metadew.iesi.datatypes.DataType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class Array implements DataType {

    private final List<DataType> list;

    public Array() {
        this.list = new ArrayList<>();
    }

    public String toString() {
        return "{{^list(" + list.stream().map(DataType::toString).collect(Collectors.joining(", ")) + ")}}";
    }

    public void add(DataType element) {
        list.add(element);
    }

}
