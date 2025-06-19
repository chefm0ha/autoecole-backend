package com.springBoot.autoEcole.service;

import java.util.Collection;
import java.util.Optional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.springBoot.autoEcole.model.User;

public interface UserService {
	User loadUserByUsername(String email) throws UsernameNotFoundException;
	User saveUser(User user);
	boolean existsByEmail(String email);
}