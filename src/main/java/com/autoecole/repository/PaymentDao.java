package com.autoecole.repository;

import java.util.Collection;

import com.autoecole.model.ApplicationFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Payment;

@Repository
@Transactional
public interface PaymentDao extends CrudRepository<Payment, Long> {
	Collection<Payment> findAll();
	Long removeById(Long id);
	Payment findByApplicationFile(ApplicationFile applicationFile);
}