package io.metadew.iesi.server.rest.template.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MatcherDto extends RepresentationModel<MatcherDto> {
    private String type;
    private String key;
    private MatcherValueDto matcherValueDto;
}
