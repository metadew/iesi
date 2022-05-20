package io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.Policy;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequestLabel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionRequestLabelPolicy implements Policy<List<ExecutionRequestLabel>> {
    private String name;
    private boolean disabled;

    @Override
    public boolean verify(List<ExecutionRequestLabel> executionRequestLabels) {
        if (disabled) {
            return true;
        }
        ExecutionRequestLabel executionRequestLabelFound = executionRequestLabels.stream()
                .filter(executionRequestLabel -> executionRequestLabel.getName().equals(name))
                .findFirst()
                .orElse(null);
        return executionRequestLabelFound != null;
    }
}
