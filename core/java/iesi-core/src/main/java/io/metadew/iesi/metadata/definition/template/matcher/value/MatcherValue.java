package io.metadew.iesi.metadata.definition.template.matcher.value;

import io.metadew.iesi.metadata.definition.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class MatcherValue extends Metadata<MatcherValueKey> {

    public MatcherValue(MatcherValueKey metadataKey) {
        super(metadataKey);
    }

}
