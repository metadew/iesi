package io.metadew.iesi.metadata.definition.impersonation.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImpersonationParameterKey extends MetadataKey {
    @Builder
    public ImpersonationParameterKey(ImpersonationKey impersonationKey, String parameterName) {
        this.impersonationKey = impersonationKey;
        this.parameterName = parameterName;
    }

    private final ImpersonationKey impersonationKey;
    private final String parameterName;

}
