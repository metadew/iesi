package io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.Policy;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;

import java.util.List;

public class ScriptLabelPolicy implements Policy<List<ScriptLabel>> {
    private String name;

    @Override
    public boolean verify(List<ScriptLabel> scriptLabels) {
        ScriptLabel scriptLabelFound = scriptLabels.stream()
                .filter(scriptLabel -> scriptLabel.getName().equals(name))
                .findFirst()
                .orElse(null);
        return scriptLabelFound != null;
    }

    public String getName() {
        return name;
    }
}
