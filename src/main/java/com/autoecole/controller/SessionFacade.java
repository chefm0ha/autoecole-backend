package com.autoecole.controller;

import java.util.Collection;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.autoecole.model.Session;
import com.autoecole.service.SessionService;

@RestController
@RequestMapping("/session")
@CrossOrigin
@AllArgsConstructor
public class SessionFacade {

	private final SessionService sessionService;

	@GetMapping("/getAllSessions")
	public Collection<Session> getAllSessions() {
		return sessionService.findAllSessions();
	}

	@PostMapping("/saveSession/{candidateCin}")
	public Session saveSession(
			@PathVariable String candidateCin,
			@RequestBody Session session) {
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