package io.metadew.iesi.metadata.definition.template;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.template.matcher.Matcher;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(using = TemplateJsonComponent.Deserializer.class)
public class Template extends Metadata<TemplateKey> implements DataType {

    private final String name;
    private final Long version;
    private final String description;
    private List<Matcher> matchers;

    @Builder
    public Template(TemplateKey metadataKey, String name, Long version, String description, List<Matcher> matchers) {
        super(metadataKey);
        this.name = name;
        this.version = version;
        this.description = description;
        this.matchers = matchers;
    }

    @Override
    public String toString() {
        return "{{^template(" + name + ", " + version + "}}";
    }

    public void addMatcher(Matcher matcher) {
        this.matchers.add(matcher);
    }

}
