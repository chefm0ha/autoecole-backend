package com.springBoot.autoEcole.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.bean.ReportingPayementBean;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.repository.ICandidatDao;
import com.springBoot.autoEcole.service.CandidatService;
@Service
@Transactional
public class CandidatServiceImpl implements CandidatService{

	@Autowired
	private ICandidatDao candidatDao;
	
	@Override
	public Collection<Candidate> findActifCandidat(Boolean actif) {
		return candidatDao.findByActif(actif);
	}
	
	@Override
	public Candidate saveCandidat(Candidate candidate) {
		candidate.setId(candidate.getCin()+ candidate.getCategory());
		return candidatDao.save(candidate);
	}

	@Override
	public Long deleteCandidat(String id) {
		return candidatDao.removeById(id);
	}

	@Override
	public Candidate findByCin(String cin) {
		return candidatDao.findByCin(cin);
	}

	@Override
	public Candidate findById(String id) {
		Candidate candidate = new Candidate();
		if(candidatDao.findById(id).isPresent()) {
			candidate = candidatDao.findById(id).get();
		}
		return candidate;
	}
	@Override
	public List<ReportingPayementBean> getReportingPayment() {
		List<ReportingPayementBean> reportingPayments= new ArrayList<ReportingPayementBean>();
		for(Candidate candidate : findActifCandidat(true)) {
			ReportingPayementBean reportingPayment = new ReportingPayementBean();
			reportingPayment.setFullName(candidate.getLastName()+" "+ candidate.getFirstName());
			reportingPayment.setInitialPrice(candidate.getInitialPrice());
			Integer paid = candidate.getPayment().stream()
					.map(payment->payment.getMontant()).reduce((x, y) -> x+y).orElse(0);
			reportingPayment.setPaid(paid);
			if(candidate.getInitialPrice()!=null && paid!=null) {
				reportingPayment.setRest(candidate.getInitialPrice() - paid);
			}
			reportingPayments.add(reportingPayment);
			}
		
		 
		
		
		return reportingPayments;
	}

	@Override
	public Integer findCandidatsYear() {
		Instant now =Instant.now();
		Date today= Date.from(now);
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		int year = cal.get(Calendar.YEAR);
		Date firstDayInYear = null;
		try {
			firstDayInYear = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/"+year);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return candidatDao.findCandidatsYear(firstDayInYear);
	}

	@Override
	public Candidate findByIdAndActif(String id, Boolean actif) {
		return candidatDao.findByIdAndActif(id,actif);
	}
}
