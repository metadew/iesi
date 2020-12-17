package io.metadew.iesi.server.rest.configuration.security;

import io.metadew.iesi.metadata.definition.user.User;
import io.metadew.iesi.metadata.definition.user.UserKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
public class WithMockIesiUserSecurityContextFactory implements WithSecurityContextFactory<WithIesiUser> {

	@Override
	public SecurityContext createSecurityContext(WithIesiUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		User user = User.builder()
				.userKey(new UserKey(UUID.randomUUID()))
				.username(customUser.username())
				.password(customUser.password())
				.expired(customUser.expired())
				.locked(customUser.locked())
				.enabled(customUser.enabled())
				.credentialsExpired(customUser.credentialsExpired())
				.roleKeys(new HashSet<>())
				.build();

		IesiUserDetails principal = new IesiUserDetails(user, Arrays.stream(customUser.authorities())
				.map(IESIGrantedAuthority::new)
				.collect(Collectors.toSet()));
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, customUser.password(), principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}

}