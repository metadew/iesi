package io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyDefinition;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyVerificationException;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionRequestPolicyDefinition extends PolicyDefinition<ExecutionRequest> {
    private List<ExecutionRequestLabelPolicy> labels;

    @Override
    public void verify(ExecutionRequest toVerify) {
       for (ExecutionRequestLabelPolicy label : labels) {
           if (!label.verify(new ArrayList<>(toVerify.getExecutionRequestLabels()))) {
               throw new PolicyVerificationException(String.format(
                       "%s does not contain the mandatory label \"%s\" defined in the policy \"%s\"",
                       toVerify.getName(),
                       label.getName(),
                       super.getName()
               ));
           }
       }
    }
}
