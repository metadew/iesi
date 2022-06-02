package io.metadew.iesi.server.rest.template.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract  class MatcherValueDto extends RepresentationModel<MatcherValueDto> {

}
