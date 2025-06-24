package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.dto.CandidateDetailsDTO;
import com.springBoot.autoEcole.dto.CandidateListDTO;
import com.springBoot.autoEcole.dto.CandidateSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.service.CandidateService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/candidate")
@CrossOrigin
public class CandidateFacade {
	
	@Autowired
	private CandidateService candidateService;

	@GetMapping("/getCandidate/{cin}")
	public Candidate getCandidateByCin(@PathVariable @NotNull String cin) { return candidateService.findByCin(cin); }

	@GetMapping("/getCandidateDetails/{cin}")
	public CandidateDetailsDTO getCandidateDetails(@PathVariable @NotNull String cin) {
		return candidateService.getCandidateDetails(cin);
	}

	@PostMapping("/saveCandidate")
	public Candidate saveCandidate(@RequestBody Candidate candidate) {
		return candidateService.saveCandidate(candidate);
	}

	@PutMapping("/updateCandidate/{cin}")
	public Candidate updateCandidate(@PathVariable @NotNull String cin, @RequestBody Candidate candidateUpdates) {
		return candidateService.updateCandidate(cin, candidateUpdates);
	}

	@DeleteMapping("/deleteCandidate/{cin}")
	public Long deleteCandidate(@PathVariable @NotNull String cin) {
		return candidateService.deleteCandidate(cin);
	}

	@GetMapping("/getActiveCandidates")
	public Page<CandidateListDTO> getActiveCandidates(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "cin") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDirection) {

		Sort sort = sortDirection.equalsIgnoreCase("desc")
				? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);
		return candidateService.findActiveCandidatesDTO(true, pageable);
	}

	@GetMapping("/search")
	public Page<CandidateListDTO> searchCandidatesWithParams(
			@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName,
			@RequestParam(required = false) String cin,
			@RequestParam(required = false) Boolean isActive,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "cin") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDirection) {

		CandidateSearchDTO searchCriteria = CandidateSearchDTO.builder()
				.firstName(firstName)
				.lastName(lastName)
				.cin(cin)
				.isActive(isActive)
				.build();

		Sort sort = sortDirection.equalsIgnoreCase("desc")
				? Sort.by(sortBy).descending()
				: Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);
		return candidateService.searchCandidatesDTO(searchCriteria, pageable);
	}
}