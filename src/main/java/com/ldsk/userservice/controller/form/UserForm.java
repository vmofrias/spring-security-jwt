package com.ldsk.userservice.controller.form;

import org.springframework.beans.factory.annotation.Autowired;

import com.ldsk.userservice.domain.User;
import com.ldsk.userservice.repository.UserRepository;

import lombok.Data;

@Data
public class UserForm {
	private String name;
	private String username;
	private String password;
	
	@Autowired
	private UserRepository userRepo;
	
	public User result() {
		User user = new User();
		
		user.setName(name);
		user.setUsername(username);
		user.setPassword(password);
		
		return user;
	}
}
