package io.metadew.iesi.server.rest.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.definition.User;
import io.metadew.iesi.server.rest.controller.FrameworkConnection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private static UserConfiguration userConfiguration = new UserConfiguration(
			FrameworkConnection.getInstance().getFrameworkExecution());

	public CustomUserDetailsService() {
		super();
	}

	@Override
	public UserDetails loadUserByUsername(final String username) {
		User user = userConfiguration.getUser(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new UserPrincipal(user);
	}
}



