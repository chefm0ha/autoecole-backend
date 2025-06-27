package com.springBoot.autoEcole.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.PaymentInstallment;

@Repository
@Transactional
public interface PaymentInstallmentDao extends CrudRepository<PaymentInstallment, Long> {
    Long removeById(Long id);

    @Modifying
    @Query(value = "CALL save_payment_installment(:p_payment_id, :p_amount)", nativeQuery = true)
    void savePaymentInstallmentWithProcedure(
            @Param("p_payment_id") Long paymentId,
            @Param("p_amount") Integer amount
    );
}