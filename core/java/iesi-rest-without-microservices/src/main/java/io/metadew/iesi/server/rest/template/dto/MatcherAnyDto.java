package io.metadew.iesi.server.rest.template.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonTypeName;

@Data
@EqualsAndHashCode(callSuper = false)
// @JsonTypeName("any")
public class MatcherAnyDto extends MatcherValueDto {
    public MatcherAnyDto() {
        super("any");
    }
}
