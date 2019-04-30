package io.metadew.iesi.server.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
//@EnableDiscoveryClient
public class IesiRestApplication  {


	public static void main(String[] args) {
		SpringApplication.run(IesiRestApplication.class, args);
	}
}