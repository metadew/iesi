package io.metadew.iesi.server.rest.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class MatcherDto extends RepresentationModel<MatcherDto> {
    private String key;
    private MatcherValueDto matcherValue;
}
