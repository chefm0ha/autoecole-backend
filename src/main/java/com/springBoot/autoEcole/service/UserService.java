package com.springBoot.autoEcole.service;

import java.util.Collection;
import com.springBoot.autoEcole.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
	User findUserByEmail(String email) throws UsernameNotFoundException;
	User saveUser(User user);
	boolean existsByEmail(String email);
	Collection<User> findAllUsers();
	Long deleteUser(Long id);
}