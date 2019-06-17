package iesi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.stereotype.Controller;


@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
@Controller
public class IesiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(IesiGatewayApplication.class, args);
	}

//	@Bean
//	public CustomFilter customFilter() {
//	    return new CustomFilter();
//	}

}
