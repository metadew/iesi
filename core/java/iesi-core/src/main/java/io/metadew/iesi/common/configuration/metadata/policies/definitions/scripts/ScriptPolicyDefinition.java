package io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyDefinition;

import java.util.List;

public class ScriptPolicyDefinition extends PolicyDefinition {
    private List<ScriptLabelPolicy> labels;

    public List<ScriptLabelPolicy> getLabels() {
        return labels;
    }
}
