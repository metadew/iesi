package io.metadew.iesi.metadata.definition.template;

import io.metadew.iesi.metadata.definition.key.MetadataKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateKey extends MetadataKey {

    private final String id;

}