package com.autoecole.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.PaymentInstallment;

@Repository
@Transactional
public interface PaymentInstallmentDao extends CrudRepository<PaymentInstallment, Long> {
    Long removeById(Long id);
}