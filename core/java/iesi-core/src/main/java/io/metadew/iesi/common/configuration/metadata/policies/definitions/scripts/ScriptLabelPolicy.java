package io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.Policy;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;

import java.util.List;

public class ScriptLabelPolicy implements Policy<List<ScriptLabel>> {
    private String name;

    @Override
    public boolean verify(List<ScriptLabel> scriptLabels) {
        for (ScriptLabel scriptLabel : scriptLabels) {
            if (scriptLabel.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }
}
