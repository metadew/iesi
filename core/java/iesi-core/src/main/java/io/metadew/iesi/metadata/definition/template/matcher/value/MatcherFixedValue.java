package io.metadew.iesi.metadata.definition.template.matcher.value;

import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
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
    public MatcherFixedValue(MatcherValueKey metadataKey, MatcherKey matcherKey, String value) {
        super(metadataKey, matcherKey);
        this.value = value;
    }

}
