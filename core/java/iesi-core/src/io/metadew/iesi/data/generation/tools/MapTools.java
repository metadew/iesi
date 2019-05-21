package io.metadew.iesi.data.generation.tools;

import java.util.Map;

public class MapTools {

    public MapTools() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map deepMerge(Map original, Map newMap) {
        for (Object key : newMap.keySet()) {
            if (newMap.get(key) instanceof Map && original.get(key) instanceof Map) {
                Map originalChild = (Map) original.get(key);
                Map newChild = (Map) newMap.get(key);
                original.put(key, deepMerge(originalChild, newChild));
            } else {
                original.put(key, newMap.get(key));
            }
        }
        return original;
    }
}
