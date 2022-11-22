package io.metadew.iesi.server.rest.template.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
// @JsonTypeName("fixed")
public class MatcherFixedDto extends MatcherValueDto {
    private String value;
    public MatcherFixedDto(String value) {
        super("fixed");
        this.value = value;
    }

    public MatcherFixedDto() {
        super("fixed");
    }
}
