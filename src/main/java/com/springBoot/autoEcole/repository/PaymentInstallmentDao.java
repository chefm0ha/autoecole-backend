package com.springBoot.autoEcole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.PaymentInstallment;

@Repository
@Transactional
public interface PaymentInstallmentDao extends CrudRepository<PaymentInstallment, Long> {
    Long removeById(Long id);
}