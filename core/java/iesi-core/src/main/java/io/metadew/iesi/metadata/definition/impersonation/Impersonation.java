package io.metadew.iesi.metadata.definition.impersonation;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.impersonation.key.ImpersonationKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Impersonation extends Metadata<ImpersonationKey> {

    private String description;
    private List<ImpersonationParameter> parameters = new ArrayList<>();

    public Impersonation(ImpersonationKey impersonationKey, String description, List<ImpersonationParameter> parameters) {
        super(impersonationKey);
        this.description = description;
        this.parameters = parameters;
    }
}