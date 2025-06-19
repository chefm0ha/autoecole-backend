package com.springBoot.autoEcole.service.impl;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.repository.CandidateDao;
import com.springBoot.autoEcole.service.CandidateService;

@Service
@Transactional
public class CandidateServiceImpl implements CandidateService {

	@Autowired
	private CandidateDao candidateDao;

	@Override
	public Collection<Candidate> findActiveCandidates(Boolean isActive) {
		return candidateDao.findByIsActive(isActive);
	}

	@Override
	public Candidate saveCandidate(Candidate candidate) {
		return candidateDao.save(candidate);
	}

	@Override
	public Long deleteCandidate(String cin) {
		return candidateDao.removeByCin(cin);
	}

	@Override
	public Candidate findByCinAndIsActive(String cin, Boolean isActive) {
		return candidateDao.findByCinAndIsActive(cin, isActive);
	}
}