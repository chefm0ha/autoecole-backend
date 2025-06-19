package com.springBoot.autoEcole.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.repository.IPaymentDao;
import com.springBoot.autoEcole.service.CandidatService;
import com.springBoot.autoEcole.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private CandidatService candidatService;
	@Autowired
	private IPaymentDao paymentDao;

	
	@Override
	public Collection<Payment> findAllPayment() {
		return paymentDao.findAll();
	}
	
	@Override
	public Payment savePayment(String id, Payment payment) {
		Candidate candidate = candidatService.findById(id);
		Payment paymentC = new Payment();
		paymentC.setCandidate(candidate);
		paymentC.setDate(payment.getDate());
		paymentC.setMontant(payment.getMontant());
		paymentDao.save(paymentC);
		return paymentC;
	
	}

	@Override
	public Long deletePayment(Long id) {
		return paymentDao.removeById(id);
	}

}
