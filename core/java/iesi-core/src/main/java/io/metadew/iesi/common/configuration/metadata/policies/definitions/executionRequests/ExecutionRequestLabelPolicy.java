package io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.Policy;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;

import java.util.List;

public class ExecutionRequestLabelPolicy implements Policy<List<ExecutionRequestLabel>> {
    @Override
    public boolean verify(List<ExecutionRequestLabel> toVerify) {
        return false;
    }
}
