package io.metadew.iesi.metadata.definition.template.matcher.value;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MatcherAnyValue extends MatcherValue {

    @Builder
    public MatcherAnyValue(MatcherValueKey metadataKey) {
        super(metadataKey);
    }

}
