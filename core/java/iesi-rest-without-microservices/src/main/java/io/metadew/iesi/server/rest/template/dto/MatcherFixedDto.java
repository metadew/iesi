package io.metadew.iesi.server.rest.template.dto;

import lombok.*;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.springframework.stereotype.Service;

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
