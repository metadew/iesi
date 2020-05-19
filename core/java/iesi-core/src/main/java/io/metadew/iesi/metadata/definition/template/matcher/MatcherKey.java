package io.metadew.iesi.metadata.definition.template.matcher;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MatcherKey extends MetadataKey {

    private final String id;

}