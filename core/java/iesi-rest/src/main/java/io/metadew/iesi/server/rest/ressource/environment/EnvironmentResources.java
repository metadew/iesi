package io.metadew.iesi.server.rest.ressource.environment;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.server.rest.controller.EnvironmentsController;
import lombok.Getter;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
@Getter
public class EnvironmentResources extends ResourceSupport {
	@JsonProperty(value = "result")
	private final List<Environment> environment;

	public EnvironmentResources(final List<Environment> environment) {

		this.environment = (List<Environment>) environment;
		for (Environment environments : environment) {
			add(linkTo(methodOn(EnvironmentsController.class)
					.getByName(environments.getName(), null))
					.withSelfRel());
		}
	}

}