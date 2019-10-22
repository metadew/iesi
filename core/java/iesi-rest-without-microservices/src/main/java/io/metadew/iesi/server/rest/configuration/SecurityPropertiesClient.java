package io.metadew.iesi.server.rest.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
@Configuration
@ConfigurationProperties("securitykey.jwt")
public class SecurityPropertiesClient {

	private Resource publicKey;

	public Resource getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(Resource publicKey) {
		this.publicKey = publicKey;
	}

}