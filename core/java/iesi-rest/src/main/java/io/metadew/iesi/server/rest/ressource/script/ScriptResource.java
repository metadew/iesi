package io.metadew.iesi.server.rest.ressource.script;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.server.rest.controller.ScriptController;
import lombok.Getter;

@Getter
public class ScriptResource extends ResourceSupport{
	@JsonProperty("result")
	private final Script script;
	
	public ScriptResource(final Script script, String name) {
		this.script = script;	
		add(linkTo(ScriptController.class).withSelfRel());
//		add(linkTo(methodOn(ScriptController.class)
//				.getByName(name))
//				.withSelfRel());
        add(linkTo(ScriptController.class).withRel("environment"));
		
	}
}