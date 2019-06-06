package io.metadew.iesi.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

	@Autowired
	private UserDetailsManager usersJdbc;

	@GetMapping("/myaccount")
	public ResponseEntity<Principal> get(final Principal principal) {
		if (!principal.getName().isEmpty()) {
			return ResponseEntity.ok(principal);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@GetMapping("/users/{name}")
	public ResponseEntity<UserDetails> getUser(@PathVariable String name) {
		return ResponseEntity.status(HttpStatus.OK).body(usersJdbc.loadUserByUsername(name));
	}
}
