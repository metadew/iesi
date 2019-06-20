package io.metadew.iesi.server.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

	@GetMapping("/myaccount")
	public ResponseEntity<Principal> get(final Principal principal) {
		if (!principal.getName().isEmpty()) {
			return ResponseEntity.ok(principal);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
}
