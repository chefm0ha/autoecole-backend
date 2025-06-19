package com.springBoot.autoEcole.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.User;

@Repository
@Transactional
public interface UserDao extends CrudRepository<User, Integer> {

	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
}
