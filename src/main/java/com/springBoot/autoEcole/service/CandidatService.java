package com.springBoot.autoEcole.service;

import java.util.Collection;
import java.util.List;

import com.springBoot.autoEcole.bean.ReportingPayementBean;
import com.springBoot.autoEcole.model.Candidate;

public interface CandidatService {
	public Collection<Candidate> findActifCandidat(Boolean actif);

	public Candidate saveCandidat(Candidate candidate);

	public Long deleteCandidat(String id);
	
	public Candidate findByCin(String cin);
	
	public Candidate findById(String id);

	public Candidate findByIdAndActif(String id, Boolean actif);
	
	public List<ReportingPayementBean> getReportingPayment();

	public Integer findCandidatsYear();
	
}
