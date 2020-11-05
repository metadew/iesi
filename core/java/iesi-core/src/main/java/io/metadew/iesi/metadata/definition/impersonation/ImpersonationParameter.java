package io.metadew.iesi.metadata.definition.impersonation;


import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationParameterKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImpersonationParameter extends Metadata<ImpersonationParameterKey> {

    private String impersonatedConnection;
    private String description;

    //Constructors
    public ImpersonationParameter(String impersonationName, String connection, String impersonatedConnection, String description) {
        super(new ImpersonationParameterKey(new ImpersonationKey(impersonationName), connection));
        this.impersonatedConnection = impersonatedConnection;
        this.description = description;
    }

    @Builder
    public ImpersonationParameter(ImpersonationParameterKey impersonationParameterKey, String impersonatedConnection, String description){
        super(impersonationParameterKey);
        this.impersonatedConnection = impersonatedConnection;
        this.description = description;
    }

}