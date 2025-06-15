package com.springBoot.autoEcole.web.rest;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springBoot.autoEcole.bean.ReportingPayementBean;
import com.springBoot.autoEcole.model.Candidat;
import com.springBoot.autoEcole.service.CandidatService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/candidat")
@CrossOrigin
public class CandidatFacade {
	
	@Autowired
	private CandidatService candidatService;
	
	@GetMapping("/getActifCandidat") 
	public Collection<Candidat> getActifCandidat() {
		return  candidatService.findActifCandidat(true);			
	}
	
	@GetMapping("/getActifCandidat/{id}") 
	public Candidat getActifCandidatById(@PathVariable @NotNull String id) {
		return  candidatService.findByIdAndActif(id,true);
	}
	@PostMapping("/saveCandidat") 
	public Candidat saveCandidat(@RequestBody Candidat candidat) {
		return  candidatService.saveCandidat(candidat);			
	}
	
	@GetMapping("/deleteCandidat/{id}") 
	public Long deleteCandidat(@PathVariable @NotNull String id) {
		return  candidatService.deleteCandidat(id);			
	}
	
	@GetMapping("/getCandidat/{id}") 
	public Candidat getCandidatById(@PathVariable @NotNull String id) {
		return  candidatService.findById(id);
	}
	@GetMapping("/getReportingPayment") 
	public List<ReportingPayementBean> getReportingPayment() {
		return  candidatService.getReportingPayment();	
	}
	
	@GetMapping("/getCountActifCandidats") 
	public Integer getCountActifCandidats() {
		return  candidatService.findActifCandidat(true).size();	
	}
	
	@GetMapping("/getCountCandidatYear") 
	public Integer getCountCandidatYear(){
		return  candidatService.findCandidatsYear();	
	}
}