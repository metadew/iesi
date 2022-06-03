package io.metadew.iesi.server.rest.template.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MatcherAnyDto.class, name = "any"),
        @JsonSubTypes.Type(value = MatcherFixedDto.class, name = "fixed"),
        @JsonSubTypes.Type(value = MatcherTemplateDto.class, name = "template")
})
@AllArgsConstructor
@NoArgsConstructor
public abstract class MatcherValueDto extends RepresentationModel<MatcherValueDto> {
    @Getter
    @Setter
    private String type;
}
