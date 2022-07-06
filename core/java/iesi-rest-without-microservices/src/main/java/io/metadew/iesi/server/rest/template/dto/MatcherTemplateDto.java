package io.metadew.iesi.server.rest.template.dto;

import lombok.*;
import org.codehaus.jackson.annotate.JsonTypeName;

@Data
@EqualsAndHashCode(callSuper = false)
// @JsonTypeName("template")
public class MatcherTemplateDto extends MatcherValueDto {
    private String templateName;
    private Long templateVersion;

    public MatcherTemplateDto(String templateName, Long templateVersion) {
        super("template");
        this.templateName = templateName;
        this.templateVersion = templateVersion;
    }

    public MatcherTemplateDto() {
        super("template");
    }
}
