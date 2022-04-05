package io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.Policy;
import io.metadew.iesi.metadata.definition.script.ScriptLabel;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class ScriptLabelPolicy implements Policy<List<ScriptLabel>> {
    private String labelName;

    @Override
    public boolean verify(List<ScriptLabel> scriptLabels) {
        for (ScriptLabel scriptLabel : scriptLabels) {
            if (scriptLabel.getName().equals(labelName)) {
                return true;
            }
        }
        log.info("CANNOT FIND mandatory labels");
        return false;
    }
}
