package com.springBoot.autoEcole.web.rest;


import java.security.Principal;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springBoot.autoEcole.model.User;
import com.springBoot.autoEcole.service.UserService;
import com.sun.istack.NotNull;


@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserFacade {
	
	@Autowired
	private UserService userService;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/getUser/{login}/{password}") 
	public boolean getUser(@PathVariable @NotNull String login,@PathVariable @NotNull String password) {
		User user =	userService.loadUserByUsername(login);
		return  passwordEncoder.matches(password, user.getPassword()) && user.getUserName().equals(login);
			
	}
	
	@RequestMapping("/login")
	public boolean login(@RequestBody User user) {
		return user.getUserName().equals("user") && user.getPassword().equals("password");				
	}

	@RequestMapping("/user")
	public Principal user(HttpServletRequest request) {
		String authToken = request.getHeader("Authorization").substring("Basic".length()).trim();
		return () -> new String(Base64.getDecoder().decode(authToken)).split(":")[0];
	}

}
