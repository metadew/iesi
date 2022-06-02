package io.metadew.iesi.server.rest.template.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class MatcherTemplateDto extends MatcherValueDto {
    private final String templateName;
    private final Long templateVersion;

    public MatcherTemplateDto(String templateName, Long templateVersion) {
        super();
        this.templateName = templateName;
        this.templateVersion = templateVersion;
    }
}
