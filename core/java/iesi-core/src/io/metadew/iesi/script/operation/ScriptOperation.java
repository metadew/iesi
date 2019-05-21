package io.metadew.iesi.script.operation;

import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.Script;

import java.util.HashMap;

public class ScriptOperation {

    // Constructors
    public ScriptOperation() {

    }

    public static boolean validateScriptQuality(Script script) {
        HashMap<String, String> actionMap;
        actionMap = new HashMap<String, String>();

        boolean result = true;
        for (Action action : script.getActions()) {
            if (!actionMap.containsKey(action.getName())) {
                actionMap.put(action.getName(), action.getName());
            } else {
                result = false;
            }
        }
        return result;
    }


}