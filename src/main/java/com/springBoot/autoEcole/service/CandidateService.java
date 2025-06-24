package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.dto.CandidateDetailsDTO;
import com.springBoot.autoEcole.dto.CandidateListDTO;
import com.springBoot.autoEcole.dto.CandidateSearchDTO;
import com.springBoot.autoEcole.model.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CandidateService {
	Candidate saveCandidate(Candidate candidate);
	Long deleteCandidate(String cin);
	Candidate findByCin(String cin);
	Candidate updateCandidate(String cin, Candidate candidateUpdates);
	Page<Candidate> searchCandidates(CandidateSearchDTO searchCriteria, Pageable pageable);
	Page<CandidateListDTO> findActiveCandidatesDTO(Boolean isActive, Pageable pageable);
	Page<CandidateListDTO> searchCandidatesDTO(CandidateSearchDTO searchCriteria, Pageable pageable);
	CandidateDetailsDTO getCandidateDetails(String cin);
}