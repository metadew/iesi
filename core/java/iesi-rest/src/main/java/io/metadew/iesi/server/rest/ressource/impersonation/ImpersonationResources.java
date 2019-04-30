package io.metadew.iesi.server.rest.ressource.impersonation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.metadew.iesi.metadata.definition.Impersonation;
import io.metadew.iesi.server.rest.controller.ImpersonationController;
import lombok.Getter;

@Getter
public class ImpersonationResources extends ResourceSupport{
	@JsonProperty("result")
	private final List<Impersonation> impersonation;
	
	public ImpersonationResources(final List<Impersonation> impersonation) {
		this.impersonation = (List<Impersonation>) impersonation;	
		add(linkTo(ImpersonationController.class).withSelfRel());
        add(linkTo(ImpersonationController.class).withRel("impersonation"));
		
	}
}

