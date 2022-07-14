package io.metadew.iesi.server.rest.template.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
// @JsonTypeName("any")
public class MatcherAnyDto extends MatcherValueDto {
    public MatcherAnyDto() {
        super("any");
    }
}
