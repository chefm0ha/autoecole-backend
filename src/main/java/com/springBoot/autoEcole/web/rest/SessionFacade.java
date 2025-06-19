package com.springBoot.autoEcole.web.rest;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.Session;
import com.springBoot.autoEcole.service.SessionService;

@RestController
@RequestMapping("/session")
@CrossOrigin
public class SessionFacade {

	@Autowired
	private SessionService sessionService;

	@GetMapping("/getAllSessions")
	public Collection<Session> getAllSessions() {
		return sessionService.findAllSessions();
	}

	@PostMapping("/saveSession/{candidateCin}")
	public Session saveSession(@PathVariable String candidateCin, @RequestBody Session session) {
		return sessionService.saveSession(candidateCin, session);
	}

	@GetMapping("/getSession/{id}")
	public Session getSessionById(@PathVariable Long id) {
		return sessionService.findById(id);
	}

	@GetMapping("/deleteSession/{id}")
	public Long deleteSession(@PathVariable Long id) {
		return sessionService.deleteSession(id);
	}

	@GetMapping("/getSessionsByCandidate/{candidateCin}")
	public Collection<Session> getSessionsByCandidate(@PathVariable String candidateCin) {
		return sessionService.getSessionsOrderByDate(candidateCin);
	}
}