package io.metadew.iesi.data.definition;

import java.util.List;

public class DataTable {

    private String name;
    private List<DataRow> rows;

    //Constructors
    public DataTable() {

    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataRow> getRows() {
        return rows;
    }

    public void setRows(List<DataRow> rows) {
        this.rows = rows;
    }


}