package io.metadew.iesi.server.rest.user;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

public class DefaultAuthenticationProvider implements AuthenticationProvider {

	private final UserRepository userRepository;

	public DefaultAuthenticationProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

		if (authentication.getName() == null || authentication.getCredentials() == null) {
			return null;
		}

		if (authentication.getName().isEmpty() || authentication.getCredentials().toString().isEmpty()) {
			return null;
		}

		final Optional<User> user = this.userRepository.findById(authentication.getName());

		if (user.isPresent()) {
			final User userGet = user.get();
			final String providedUsername = authentication.getName();
			final Object providedUserPassword = authentication.getCredentials();

			if (providedUsername.equalsIgnoreCase(userGet.getUsername())
					&& providedUserPassword.equals(userGet.getPassword())) {
				return new UsernamePasswordAuthenticationToken(userGet.getUsername(), userGet.getPassword(),
						Collections.singleton(new SimpleGrantedAuthority(userGet.getUserRole())));
			}
		}

		throw new UsernameNotFoundException("Invalid username or password.");
	}

	@Override
	public boolean supports(final Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}