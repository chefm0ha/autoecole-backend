package com.springBoot.autoEcole.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.User;
import com.springBoot.autoEcole.repository.UserDao;
import com.springBoot.autoEcole.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
    @Override
    public User loadUserByUsername(String userName) throws UsernameNotFoundException {
    	Optional<User> optionalUser = userDao.findByUserName(userName);
        if(optionalUser.isPresent()) {
        	User users = optionalUser.get();        	
            return  User.builder()
            	.userName(users.getUserName())
            	.password(users.getPassword())
            	.build();
        } else {
        	throw new UsernameNotFoundException("User Name is not Found");
        }   
    }

    @Override
	public Optional<User> findByUserName(String userName) {
		Optional<User>  user = userDao.findByUserName(userName);
		return user;
	}

}
