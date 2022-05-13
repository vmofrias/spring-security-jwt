package com.ldsk.userservice.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldsk.userservice.controller.form.UserForm;
import com.ldsk.userservice.domain.Role;
import com.ldsk.userservice.domain.User;
import com.ldsk.userservice.service.UserService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api") @Slf4j
public class UserController {
	private static final String AUTHORIZATION = "Authorization";
	private static final String APPLICATION_JSON_VALUE = "application_json";
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers(){
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	@PostMapping("/users/save")
	public ResponseEntity<User> saveUser(@RequestBody UserForm form){
		User user = form.result();
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveUser(user));
	}
	
	@PostMapping("/role/save")
	public ResponseEntity<Role> saveRole(@RequestBody Role role){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveRole(role));
	}
	
	@PostMapping("/role/addroletouser")
	public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form){
		userService.addRoleToUser(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String authorizationHeader = request.getHeader(AUTHORIZATION);
		
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				String refresh_token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refresh_token);
				String username = decodedJWT.getSubject();
				User user = userService.getUser(username);
				
				String access_token = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.sign(algorithm);
				Map<String, String> tokens = new HashMap<>();
				
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);
				
				response.setContentType(APPLICATION_JSON_VALUE);
				
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			} catch (Exception exception) {
				log.error("Error logging in: {}", exception.getMessage());
				response.setHeader("error", exception.getMessage());
				response.setStatus(403);
				//response.sendError(403);
				Map<String, String> error = new HashMap<>();
				
				error.put("error_message", exception.getMessage());
				
				response.setContentType(APPLICATION_JSON_VALUE);
				
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
			
		}else {
			throw new RuntimeException("Refresh token is missing.");
		}
	}
}

@Data
class RoleToUserForm {
	private String username;
	private String roleName;
}
