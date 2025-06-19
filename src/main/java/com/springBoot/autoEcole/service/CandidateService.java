package com.springBoot.autoEcole.service;

import java.util.Collection;

import com.springBoot.autoEcole.model.Candidate;

public interface CandidateService {

	Candidate saveCandidate(Candidate candidate);

	Long deleteCandidate(String cin);

	Collection<Candidate> findActiveCandidates(Boolean isActive);

	Candidate findByCinAndIsActive(String cin, Boolean isActive);

}