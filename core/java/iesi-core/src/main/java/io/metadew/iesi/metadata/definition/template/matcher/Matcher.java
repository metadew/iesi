package io.metadew.iesi.metadata.definition.template.matcher;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Matcher extends Metadata<MatcherKey> {

    private final String key;
    private final MatcherValue matcherValue;

    public Matcher(MatcherKey matcherKey, String key, MatcherValue matcherValue) {
        super(matcherKey);
        this.key = key;
        this.matcherValue = matcherValue;
    }

}
