package io.metadew.iesi.metadata.definition;

public class Transformation {

    private long number;
    private String type = "default";
    private String leftField;
    private String rightField;

    //Constructors
    public Transformation() {

    }

    //Getters and Setters
    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLeftField() {
        return leftField;
    }

    public void setLeftField(String leftField) {
        this.leftField = leftField;
    }

    public String getRightField() {
        return rightField;
    }

    public void setRightField(String rightField) {
        this.rightField = rightField;
    }


}