package io.metadew.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
//https://www.javadevjournal.com/spring-boot/spring-boot-admin/

@SpringBootApplication
@EnableAdminServer
public class IesiRestAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(IesiRestAdminApplication.class, args);
	}
}
