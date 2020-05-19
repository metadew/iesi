package io.metadew.iesi.metadata.definition.template.matcher.value;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MatcherTemplate extends MatcherValue {

    private final String templateName;

    public MatcherTemplate(MatcherValueKey metadataKey, String templateName) {
        super(metadataKey);
        this.templateName = templateName;
    }

}
