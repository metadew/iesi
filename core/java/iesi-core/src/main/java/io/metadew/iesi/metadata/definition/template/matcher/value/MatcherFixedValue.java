package io.metadew.iesi.metadata.definition.template.matcher.value;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MatcherFixedValue extends MatcherValue {

    private final String value;

    @Builder
    public MatcherFixedValue(MatcherValueKey metadataKey, String value) {
        super(metadataKey);
        this.value = value;
    }

}
