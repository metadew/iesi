package io.metadew.iesi.server.rest.configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

@Profile({"oauth"})
@PropertySource({"classpath:application-oauth.yml"})
@ConfigurationProperties("publickey")
public class SecurityPropertiesClient {

	private JwtProperties jwt;

	public JwtProperties getJwt() {
		return jwt;
	}

	public void setJwt(JwtProperties jwt) {
		this.jwt = jwt;
	}

	public static class JwtProperties {

		private Resource publicKey;

		public Resource getPublicKey() {
			return publicKey;
		}

		public void setPublicKey(Resource publicKey) {
			this.publicKey = publicKey;
		}
	}

}