package com.autoecole.service;

import com.autoecole.dto.response.CandidateDetailsDTO;
import com.autoecole.dto.response.CandidateListDTO;
import com.autoecole.dto.request.CandidateSearchDTO;
import com.autoecole.model.Candidate;
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
	Long countActiveCandidates();
}