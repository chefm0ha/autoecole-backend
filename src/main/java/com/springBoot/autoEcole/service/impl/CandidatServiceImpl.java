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
import com.springBoot.autoEcole.model.Candidat;
import com.springBoot.autoEcole.repository.ICandidatDao;
import com.springBoot.autoEcole.service.CandidatService;
@Service
@Transactional
public class CandidatServiceImpl implements CandidatService{

	@Autowired
	private ICandidatDao candidatDao;
	
	@Override
	public Collection<Candidat> findActifCandidat(Boolean actif) {
		return candidatDao.findByActif(actif);
	}
	
	@Override
	public Candidat saveCandidat(Candidat candidat) {
		candidat.setId(candidat.getCin()+candidat.getCategory());
		return candidatDao.save(candidat);
	}

	@Override
	public Long deleteCandidat(String id) {
		return candidatDao.removeById(id);
	}

	@Override
	public Candidat findByCin(String cin) {
		return candidatDao.findByCin(cin);
	}

	@Override
	public Candidat findById(String id) {
		Candidat candidat = new Candidat();
		if(candidatDao.findById(id).isPresent()) {
			candidat= candidatDao.findById(id).get();
		}
		return candidat;
	}
	@Override
	public List<ReportingPayementBean> getReportingPayment() {
		List<ReportingPayementBean> reportingPayments= new ArrayList<ReportingPayementBean>();
		for(Candidat candidat : findActifCandidat(true)) {
			ReportingPayementBean reportingPayment = new ReportingPayementBean();
			reportingPayment.setFullName(candidat.getLastName()+" "+candidat.getFirstName());
			reportingPayment.setInitialPrice(candidat.getInitialPrice());
			Integer paid = candidat.getPayment().stream()
					.map(payment->payment.getMontant()).reduce((x, y) -> x+y).orElse(0);
			reportingPayment.setPaid(paid);
			if(candidat.getInitialPrice()!=null && paid!=null) {
				reportingPayment.setRest(candidat.getInitialPrice() - paid);
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
	public Candidat findByIdAndActif(String id, Boolean actif) {
		return candidatDao.findByIdAndActif(id,actif);
	}
}
