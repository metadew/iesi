package io.metadew.iesi.server.rest.template.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Relation(value = "template", collectionRelation = "templates")
public class TemplateDto extends RepresentationModel<TemplateDto> {
    private UUID uuid;
    private String name;
    private Long version;
    private String description;
    private Set<MatcherDto> matchers;
}
