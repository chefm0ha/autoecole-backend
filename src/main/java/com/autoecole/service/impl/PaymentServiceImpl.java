package com.autoecole.service.impl;

import java.util.Collection;

import com.autoecole.dto.PaymentWithInstallmentsDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.ApplicationFile;
import com.autoecole.model.Payment;
import com.autoecole.repository.PaymentDao;
import com.autoecole.service.ApplicationFileService;
import com.autoecole.service.PaymentService;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final ApplicationFileService applicationFileService;
	private final PaymentDao paymentDao;

	@Override
	public Collection<Payment> findAllPayment() {
		return paymentDao.findAll();
	}

	@Override
	public Payment findById(Long id) {
		return paymentDao.findById(id).orElse(null);
	}

	@Override
	public PaymentWithInstallmentsDTO getPaymentByApplicationFile(Long applicationFileId) {
		ApplicationFile applicationFile = applicationFileService.findById(applicationFileId);
		if (applicationFile == null) {
			throw new EntityNotFoundException("ApplicationFile not found with ID: " + applicationFileId);
		}

		Payment payment = paymentDao.findByApplicationFile(applicationFile);
		if (payment == null) {
			return null; // No payment found for this application file
		}

		return PaymentWithInstallmentsDTO.fromEntity(payment);
	}

	@Override
	public PaymentWithInstallmentsDTO getPaymentWithInstallments(Long paymentId) {
		Payment payment = findById(paymentId);
		if (payment == null) {
			throw new EntityNotFoundException("Payment not found with ID: " + paymentId);
		}

		return PaymentWithInstallmentsDTO.fromEntity(payment);
	}
}