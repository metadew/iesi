package io.metadew.iesi.metadata.definition;


public class ResultObject {

    private String type;
    private Object data;

    // Constructors
    public ResultObject() {

    }

    public ResultObject(String type, Object data) {
        this.setType(type);
        this.setData(data);
    }

    // Getters and Setters
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}