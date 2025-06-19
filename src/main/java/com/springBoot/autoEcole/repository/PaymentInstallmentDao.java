package com.springBoot.autoEcole.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.model.PaymentInstallment;

@Repository
@Transactional
public interface PaymentInstallmentDao extends CrudRepository<PaymentInstallment, Long> {

    Long removeById(Long id);

    List<PaymentInstallment> findByPaymentOrderByInstallmentNumberAsc(Payment payment);

    List<PaymentInstallment> findByStatusAndDateBefore(String status, LocalDate date);

    List<PaymentInstallment> findByStatus(String status);
}