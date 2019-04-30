package io.metadew.iesi.server.rest.repository;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;

import io.metadew.iesi.server.rest.controller.UserController;
import io.metadew.iesi.server.rest.models.User;
import lombok.Getter;

@Getter
public class UserResource extends ResourceSupport {
	
	private final User user;
	
	  public UserResource(final User user) {
		    this.user = user;
		    final long id = user.getId();
		    add(linkTo(UserController.class).withRel("user"));
		    //add(linkTo(methodOn(ScriptController.class).getAllScript()).withRel("script").withType("GET"));
//		    add(linkTo(methodOn(EnvironmentsController.class).getAllEnvironments()).withSelfRel());
		    add(linkTo(methodOn(UserController.class).getId(id)).withSelfRel());
		  }
		}
	




