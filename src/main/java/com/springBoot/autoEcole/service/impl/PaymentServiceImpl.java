package com.springBoot.autoEcole.service.impl;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.mapper.PaymentMapper;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.repository.PaymentDao;
import com.springBoot.autoEcole.service.CandidateService;
import com.springBoot.autoEcole.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private CandidateService candidateService;

	@Autowired
	private PaymentDao paymentDao;

	@Autowired
	private PaymentMapper paymentMapper;

	@Override
	public Collection<Payment> findAllPayment() {
		return paymentDao.findAll();
	}

	@Override
	public Payment savePayment(String candidateCin, Payment payment) {
		Candidate candidate = candidateService.findByCin(candidateCin);
		Payment paymentToSave = paymentMapper.toEntity(payment, candidate);
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