package com.springBoot.autoEcole.web.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping("/saveCandidate")
	public Candidate saveCandidate(@RequestBody Candidate candidate) {
		return candidateService.saveCandidate(candidate);
	}

	@GetMapping("/deleteCandidate/{cin}")
	public Long deleteCandidate(@PathVariable @NotNull String cin) {
		return candidateService.deleteCandidate(cin);
	}
	
	@GetMapping("/getActiveCandidates")
	public Collection<Candidate> getActiveCandidates() {
		return candidateService.findActiveCandidates(true);
	}
	
	@GetMapping("/getActiveCandidate/{cin}")
	public Candidate getActiveCandidateByCin(@PathVariable @NotNull String cin) {
		return candidateService.findByCinAndIsActive(cin,true);
	}

}