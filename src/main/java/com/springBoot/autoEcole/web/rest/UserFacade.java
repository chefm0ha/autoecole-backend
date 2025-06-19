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



}
