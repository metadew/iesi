package io.metadew.iesi.server.rest.configuration.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockIesiUserSecurityContextFactory.class)
public @interface WithIesiUser {

	String username() default "iesi";
	String password() default "iesi";
	boolean credentialsExpired() default false;
	boolean enabled() default true;
	boolean expired() default false;
	boolean locked() default false;

	String[] authorities() default "";
}