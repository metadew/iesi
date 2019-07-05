package io.metadew.iesi.common.json;


public class JsonParsedItem {

    private int item;
    private int identifier;
    private int parent;
    private int iteration;
    private String key;
    private String value;
    private String path;

    public JsonParsedItem() {
    }

    // Getters and Setters
    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }


}
