package com.springBoot.autoEcole.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.springBoot.autoEcole.model.User;

public interface UserService {
	/**
	 * Récuperation Utilisateur by login
	 */
	public Optional<User> findByUserName(String userName);

	User loadUserByUsername(String userName) throws UsernameNotFoundException;
}
