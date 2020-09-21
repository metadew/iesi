package io.metadew.iesi.gcp.common.tools;

import java.util.Map;

public final class ConfigurationTools {

    @SuppressWarnings("unchecked")
    public static void update(Map<String, Object> original, Map<String, Object> update) {
        for (Map.Entry<String, Object> entry : update.entrySet()) {
            if (original.containsKey(entry.getKey()) && original.get(entry.getKey()) == null) {
                original.put(entry.getKey(), entry.getValue());
            } else if (original.containsKey(entry.getKey())) {
                if (original.get(entry.getKey()).getClass().equals(entry.getValue().getClass())) {
                    if (entry.getValue() instanceof Map) {
                        update((Map<String, Object>) original.get(entry.getKey()), (Map<String, Object>) entry.getValue());
                    } else {
                        original.put(entry.getKey(), entry.getValue());
                    }
                } else {
                    //Different structures are allowed. This makes it possible to overwrite values
                    original.putAll(update);
                }
            } else {
                original.putAll(update);
            }
        }
    }
}
