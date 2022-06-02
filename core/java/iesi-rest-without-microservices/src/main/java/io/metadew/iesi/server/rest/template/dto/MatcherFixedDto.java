package io.metadew.iesi.server.rest.template.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Data
public class MatcherFixedDto extends MatcherValueDto {
    private final String value;
    public MatcherFixedDto(String value) {
        this.value = value;
    }
}
