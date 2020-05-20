package io.metadew.iesi.metadata.definition.template.matcher;

import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.template.TemplateKey;
import io.metadew.iesi.metadata.definition.template.matcher.value.MatcherValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Matcher extends Metadata<MatcherKey> {

    private final TemplateKey templateKey;
    private final String key;
    private final MatcherValue matcherValue;

    public Matcher(MatcherKey matcherKey, TemplateKey templateKey, String key, MatcherValue matcherValue) {
        super(matcherKey);
        this.templateKey = templateKey;
        this.key = key;
        this.matcherValue = matcherValue;
    }

}
