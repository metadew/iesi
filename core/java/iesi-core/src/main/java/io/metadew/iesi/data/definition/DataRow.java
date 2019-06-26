package io.metadew.iesi.data.definition;

import java.util.List;

public class DataRow {

    private long id;
    private List<DataField> fields;

    //Constructors
    public DataRow() {

    }

    //Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<DataField> getFields() {
        return fields;
    }

    public void setFields(List<DataField> fields) {
        this.fields = fields;
    }


}