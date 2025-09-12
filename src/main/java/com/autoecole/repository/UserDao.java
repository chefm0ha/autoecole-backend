package com.autoecole.repository;

import java.util.List;
import java.util.Optional;

import com.autoecole.enums.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.autoecole.model.User;

@Repository
@Transactional
public interface UserDao extends CrudRepository<User, Long> {

	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	List<User> findByRole(UserRole role);
}
