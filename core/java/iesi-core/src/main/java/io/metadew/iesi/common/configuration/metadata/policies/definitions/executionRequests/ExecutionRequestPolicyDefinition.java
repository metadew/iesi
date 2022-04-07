package io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyDefinition;

import java.util.List;

public class ExecutionRequestPolicyDefinition extends PolicyDefinition {
    private List<ExecutionRequestLabelPolicy> labels;

    public List<ExecutionRequestLabelPolicy> getLabels() {
        return labels;
    }
}
