package io.metadew.iesi.server.rest;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class IesiRestApplicationWithoutMicroservices {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(IesiRestApplicationWithoutMicroservices.class, args);

	}
}