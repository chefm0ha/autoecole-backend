package com.springBoot.autoEcole.service.impl;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.mapper.PaymentMapper;
import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.repository.PaymentDao;
import com.springBoot.autoEcole.service.ApplicationFileService;
import com.springBoot.autoEcole.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private ApplicationFileService applicationFileService;

	@Autowired
	private PaymentDao paymentDao;

	@Autowired
	private PaymentMapper paymentMapper;

	@Override
	public Collection<Payment> findAllPayment() {
		return paymentDao.findAll();
	}

	@Override
	public Payment savePayment(Long applicationFileId, Payment payment) {
		ApplicationFile applicationFile = applicationFileService.findById(applicationFileId);
		if (applicationFile == null) {
			throw new RuntimeException("ApplicationFile not found with ID: " + applicationFileId);
		}

		Payment paymentToSave = paymentMapper.toEntity(payment, applicationFile);
		return paymentDao.save(paymentToSave);
	}

	@Override
	public Payment findById(Long id) {
		return paymentDao.findById(id).orElse(null);
	}

	@Override
	public Long deletePayment(Long id) {
		return paymentDao.removeById(id);
	}
}