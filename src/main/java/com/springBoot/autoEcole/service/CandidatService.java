package com.springBoot.autoEcole.service;

import java.util.Collection;
import java.util.List;

import com.springBoot.autoEcole.bean.ReportingPayementBean;
import com.springBoot.autoEcole.model.Candidat;

public interface CandidatService {
	public Collection<Candidat> findActifCandidat(Boolean actif);

	public Candidat saveCandidat(Candidat candidat);

	public Long deleteCandidat(String id);
	
	public Candidat findByCin(String cin);
	
	public Candidat findById(String id);

	public Candidat findByIdAndActif(String id, Boolean actif);
	
	public List<ReportingPayementBean> getReportingPayment();

	public Integer findCandidatsYear();
	
}
