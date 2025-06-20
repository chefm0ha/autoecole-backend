package com.springBoot.autoEcole.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.config.CustomUserDetails;
import com.springBoot.autoEcole.model.User;
import com.springBoot.autoEcole.repository.UserDao;
import com.springBoot.autoEcole.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userDao.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		return new CustomUserDetails(user);
	}

	@Override
	public User findUserByEmail(String email) throws UsernameNotFoundException {
		return userDao.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
	}

	@Override
	public User saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userDao.save(user);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userDao.existsByEmail(email);
	}
}