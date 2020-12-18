package io.metadew.iesi.datatypes._null;

import io.metadew.iesi.datatypes.DataType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class Null implements DataType {

    public String toString() {
        return "{{^null()}}";
    }

}
