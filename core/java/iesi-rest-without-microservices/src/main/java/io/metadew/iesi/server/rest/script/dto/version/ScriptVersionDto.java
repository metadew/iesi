package io.metadew.iesi.server.rest.script.dto.version;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.metadew.iesi.server.rest.script.dto.ScriptEmptyLinksFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScriptVersionDto extends RepresentationModel<ScriptVersionDto> {

    private long number;
    private String description;

    @JsonProperty("links")
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = ScriptEmptyLinksFilter.class)
    @Override
    public Links getLinks() {
        return super.getLinks();
    }

}