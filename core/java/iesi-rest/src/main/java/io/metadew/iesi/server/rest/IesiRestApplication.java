package io.metadew.iesi.server.rest;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IesiRestApplication {
	@Value("${http.port}")
	private int httpPort;
//	
	@Value("${server.port}")
	private int serverPortHttps;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(IesiRestApplication.class, args);

	}

	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addAdditionalTomcatConnectors(createStandardConnector());
		return tomcat;
	}
//
	private Connector createStandardConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setPort(httpPort);
//		connector.setScheme("http");
////		connector.setSecure(false);
//		connector.setPort(8443);
//		connector.setRedirectPort(8080);
		return connector;
	}
}

//curl -k -u client-id:secret -X POST https://localhost:8080/api/oauth/token\?grant_type=password\&username=admin\&password=admin
