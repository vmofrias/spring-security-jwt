package com.ldsk.userservice;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ldsk.userservice.domain.Role;
import com.ldsk.userservice.domain.User;
import com.ldsk.userservice.service.UserService;

@SpringBootApplication
public class UserserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	CommandLineRunner run(UserService userService) {
		return args ->{
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));
			
			userService.saveUser(new User(null, "Harry Potter", "harry", "1234", new ArrayList<>()));
			userService.saveUser(new User(null, "Will Smith", "will", "1234", new ArrayList<>()));
			userService.saveUser(new User(null, "Anderson Silva", "anderson", "1234", new ArrayList<>()));
			userService.saveUser(new User(null, "Elliot Alderson", "elliot", "123456", new ArrayList<>()));
			
			userService.addRoleToUser("harry", "ROLE_USER");
			userService.addRoleToUser("harry", "ROLE_MANAGER");
			userService.addRoleToUser("will", "ROLE_MANAGER");
			userService.addRoleToUser("anderson", "ROLE_ADMIN");
			userService.addRoleToUser("elliot", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("elliot", "ROLE_ADMIN");
			userService.addRoleToUser("elliot", "ROLE_USER");
		};
	}
}
