package io.metadew.iesi.metadata.definition;

import java.util.List;

public class Ledger {

    private long id;
    private String type = "default";
    private String name;
    private String description;
    private List<LedgerParameter> parameters;
    private List<LedgerItem> items;

    // Constructors
    public Ledger() {

    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<LedgerItem> getItems() {
        return items;
    }

    public void setItems(List<LedgerItem> items) {
        this.items = items;
    }

    public List<LedgerParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<LedgerParameter> parameters) {
        this.parameters = parameters;
    }

}