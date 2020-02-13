package io.metadew.iesi.metadata.definition.impersonation.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImpersonationParameterKey extends MetadataKey {

    private final ImpersonationKey impersonationKey;
    private final String parameterName;

    public ImpersonationParameterKey(String impersonationName, String parameterName) {
        this.impersonationKey = new ImpersonationKey(impersonationName);
        this.parameterName = parameterName;
    }

}
