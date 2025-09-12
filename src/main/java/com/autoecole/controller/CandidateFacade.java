package com.autoecole.controller;

import com.autoecole.dto.response.CandidateDetailsDTO;
import com.autoecole.dto.response.CandidateListDTO;
import com.autoecole.dto.request.CandidateSearchDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import com.autoecole.model.Candidate;
import com.autoecole.service.CandidateService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/candidate")
@CrossOrigin
@AllArgsConstructor
public class CandidateFacade {

	private final CandidateService candidateService;

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

	@GetMapping("/activeCandidatesNumber")
	public Long getActiveCandidatesNumber() { return candidateService.countActiveCandidates(); }

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