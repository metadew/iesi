package io.metadew.iesi.server.rest.ressource.script;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Script;
import io.metadew.iesi.server.rest.controller.ScriptController;
import lombok.Getter;

@Getter
public class ScriptResources extends ResourceSupport {
	@JsonProperty(value="result")
	private final List<Script> script;
	
	public ScriptResources(final List<Script> script) {
		this.script = (List<Script>) script;
			add(linkTo(ScriptController.class).withSelfRel());
	        add(linkTo(ScriptController.class).withRel("environment"));
			
		}
}