package io.metadew.iesi.server.rest.ressource.impersonation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.server.rest.controller.ImpersonationController;
import lombok.Getter;

@Getter
public class ImpersonationResource extends ResourceSupport {
	@JsonProperty("result")
	private final Impersonation impersonation;

	public ImpersonationResource(final Impersonation impersonation, String name) {
		this.impersonation = impersonation;
		add(linkTo(ImpersonationController.class).withSelfRel());
//		add(linkTo(methodOn(ImpersonationsController.class)
//				.getByName(name))
//				.withSelfRel());
		add(linkTo(ImpersonationController.class).withRel("impersonation"));

	}

}
