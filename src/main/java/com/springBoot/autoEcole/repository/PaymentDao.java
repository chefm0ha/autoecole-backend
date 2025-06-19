package com.springBoot.autoEcole.repository;

import java.util.Collection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Payment;

@Repository
@Transactional
public interface PaymentDao extends CrudRepository<Payment, Long> {

	Collection<Payment> findAll();

	Long removeById(Long id);
}