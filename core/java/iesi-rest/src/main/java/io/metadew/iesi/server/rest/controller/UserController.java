package io.metadew.iesi.server.rest.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.metadew.iesi.server.rest.user.User;
import io.metadew.iesi.server.rest.user.UserRepository;

@RestController
//@SessionAttributes("authorizationRequest")
public class UserController {

	@Autowired
	private UserDetailsManager usersJdbc;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/myaccount")
	public ResponseEntity<Principal> get(final Principal principal) {
		if (!principal.getName().isEmpty()) {
			return ResponseEntity.ok(principal);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
	}

	@GetMapping("/users/{name}")
	public ResponseEntity<UserDetails> getUser(@PathVariable String name) {
		return ResponseEntity.status(HttpStatus.OK).body(usersJdbc.loadUserByUsername(name));
	}

	@PostMapping("/users")
	public ResponseEntity<User> saveUser(@RequestBody User user) {
		return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(user));
	}

	@PutMapping("/users/{name}")
	public ResponseEntity<User> updateUser(@PathVariable String name, @RequestBody User user) {
		return userRepository.findById(name).map(users -> {
			users.setUsername(user.getUsername());
			users.setEnabled(user.getEnabled());
			users.setPassword(user.getPassword());
			users.setUserRole(user.getUserRole());
			User updated = userRepository.save(users);
			return ResponseEntity.ok().body(updated);
		}).orElse(ResponseEntity.notFound().build());
	}
}
