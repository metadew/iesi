package io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.Policy;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScriptLabelPolicy implements Policy<List<ScriptLabel>> {
    private String name;
    private boolean disabled;

    @Override
    public boolean verify(List<ScriptLabel> scriptLabels) {
        if (disabled) {
            return true;
        }
        ScriptLabel scriptLabelFound = scriptLabels.stream()
                .filter(scriptLabel -> scriptLabel.getName().equals(name))
                .findFirst()
                .orElse(null);
        return scriptLabelFound != null;
    }
}
