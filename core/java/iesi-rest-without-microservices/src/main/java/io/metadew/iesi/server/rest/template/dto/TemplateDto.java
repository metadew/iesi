package io.metadew.iesi.server.rest.template.dto;

import io.metadew.iesi.server.rest.script.dto.NoEmptyLinksRepresentationModel;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Relation(value = "template", collectionRelation = "templates")
public class TemplateDto extends NoEmptyLinksRepresentationModel<TemplateDto> {
    private UUID uuid;
    private String name;
    private String description;
    private Long version;
    private Set<MatcherDto> matchers;
}
