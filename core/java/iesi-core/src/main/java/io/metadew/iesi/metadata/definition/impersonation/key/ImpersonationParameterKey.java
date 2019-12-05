package io.metadew.iesi.metadata.definition.impersonation.key;

import io.metadew.iesi.metadata.definition.key.MetadataKey;

public class ImpersonationParameterKey extends MetadataKey {

    private String impersonationName;
    private String impersonationParameterName;

    public ImpersonationParameterKey(String impersonationName, String impersonationParameterName) {
        this.impersonationName = impersonationName;
        this.impersonationParameterName = impersonationParameterName;
    }

    public String getImpersonationName() {
        return impersonationName;
    }

    public String getImpersonationParameterName() {
        return impersonationParameterName;
    }
}
