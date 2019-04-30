package io.metadew.iesi.server.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.metadew.iesi.server.rest.exception.AppException;
import io.metadew.iesi.server.rest.repository.UserRepository;
import io.metadew.iesi.server.rest.repository.UserResource;
//import io.metadew.iesi.server.rest.service.UserService;

@RestController
@RequestMapping(value = "/api", produces = "application/hal+json")
public class UserController {

	@Autowired
	private UserRepository userRepository;
//	@Autowired
//	private UserService userService;

	@GetMapping("/user")
	public ResponseEntity<Resources<UserResource>> getall() {

		List<UserResource> collection = userRepository.findAll().stream().map(UserResource::new)
				.collect(Collectors.toList());
		Resources<UserResource> resources = new Resources<>(collection);
		return ResponseEntity.ok(resources);
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<UserResource> getId(@PathVariable long id) {
		return userRepository.findById(id).map(x -> ResponseEntity.ok(new UserResource(x)))
				.orElseThrow(() -> new AppException("Erreur"));
	}

}