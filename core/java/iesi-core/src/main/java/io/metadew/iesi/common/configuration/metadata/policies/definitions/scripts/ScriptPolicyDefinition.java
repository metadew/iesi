package io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts;

import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyDefinition;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyVerificationException;
import io.metadew.iesi.metadata.definition.script.Script;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ScriptPolicyDefinition extends PolicyDefinition<Script> {
    private List<ScriptLabelPolicy> labels;

    @Override
    public void verify(Script toVerify) {
        for (ScriptLabelPolicy label : labels) {
            if (!label.verify(toVerify.getLabels())) {
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
