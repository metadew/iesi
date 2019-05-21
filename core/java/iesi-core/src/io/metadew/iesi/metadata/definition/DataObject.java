package io.metadew.iesi.metadata.definition;

public class DataObject {

    private String Type;
    private Object data;

    // Constructors
    public DataObject() {

    }

    public DataObject(String type, Object data) {
        this.setType(type);
        this.setData(data);
    }

    public DataObject(ErrorObject roErrorObject) {
        this.setType("error");
        this.setData(roErrorObject);
    }

    // Getters and Setters
    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}