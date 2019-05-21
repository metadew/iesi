package io.metadew.iesi.common.json;

import java.util.ArrayList;
import java.util.List;

public class JsonParsed {

    private List<JsonParsedItem> jsonParsedItemList;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public JsonParsed() {
        this.setJsonParsedItemList(new ArrayList());
    }

    // Methods
    public void addItem(JsonParsedItem jsonParsedItem) {
        this.getJsonParsedItemList().add(jsonParsedItem);
    }

    // Getters and setters
    public List<JsonParsedItem> getJsonParsedItemList() {
        return jsonParsedItemList;
    }

    public void setJsonParsedItemList(List<JsonParsedItem> jsonParsedItemList) {
        this.jsonParsedItemList = jsonParsedItemList;
    }

}
