package io.metadew.iesi.datatypes.text;

import io.metadew.iesi.datatypes.DataType;

public class Text extends DataType {

    private final String string;

    public Text(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public String getString() {
        return string;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Text) {
            return this.string.equals(((Text) obj).getString());
        } else {
            return false;
        }
    }
}
