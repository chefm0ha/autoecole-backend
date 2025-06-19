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

import com.springBoot.autoEcole.service.DrivingSessionService;
import com.sun.istack.NotNull;

@RestController
@RequestMapping("/DrivingSession")
@CrossOrigin
public class DrivingSessionFacade {

	@Autowired
	private DrivingSessionService drivingSessionService;
	
	@PostMapping("/saveDrivingSession/{candidatId}") 
	public DrivingSession saveDrivingSession(@PathVariable String candidatId, @RequestBody DrivingSession drivingSession) {
		return drivingSessionService.saveDrivingSession(candidatId,drivingSession);			
	}
	
	@GetMapping("/deleteExam/{id}") 
	public Long deleteDrivingSession(@PathVariable @NotNull Long id) {
		return  drivingSessionService.deleteDrivingSession(id);			
	}
	
	@GetMapping("/getDrivinSessionsOrderByDate/{candidatId}") 
	public Collection<DrivingSession> getDrivinSessionsOrderByDate(@PathVariable @NotNull String candidatId) {
		  return drivingSessionService.getDrivinSessionsOrderByDate(candidatId); 
	}
}
