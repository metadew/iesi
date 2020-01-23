package io.metadew.iesi.metadata.definition.impersonation.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ImpersonationParameterKey extends MetadataKey {

    private final ImpersonationKey impersonationKey;
    private String parameterName;

    public ImpersonationParameterKey(String impersonationName, String parameterName) {
        this.impersonationKey = new ImpersonationKey(impersonationName);
        this.parameterName = parameterName;
    }

    public ImpersonationParameterKey(ImpersonationKey impersonationKey, String parameterName) {
        this.impersonationKey = impersonationKey;
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public ImpersonationKey getImpersonationKey() {
        return impersonationKey;
    }
}
