package com.autoecole.service;

import com.autoecole.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
	User findUserByEmail(String email) throws UsernameNotFoundException;
	User saveUser(User user);
	boolean existsByEmail(String email);
}