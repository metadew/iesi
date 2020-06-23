package io.metadew.iesi.metadata.definition.template.matcher.value;

import io.metadew.iesi.metadata.definition.template.matcher.MatcherKey;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MatcherTemplate extends MatcherValue {

    private final String templateName;

    @Builder
    public MatcherTemplate(MatcherValueKey metadataKey, MatcherKey matcherKey, String templateName) {
        super(metadataKey, matcherKey
        );
        this.templateName = templateName;
    }

}
