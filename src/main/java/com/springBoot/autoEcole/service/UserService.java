package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
	User findUserByEmail(String email) throws UsernameNotFoundException;
	User saveUser(User user);
	boolean existsByEmail(String email);
}