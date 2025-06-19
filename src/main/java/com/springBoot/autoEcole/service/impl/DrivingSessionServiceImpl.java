package com.springBoot.autoEcole.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.repository.SessionDao;
import com.springBoot.autoEcole.service.CandidateService;
import com.springBoot.autoEcole.service.DrivingSessionService;

@Service
@Transactional
public class DrivingSessionServiceImpl implements DrivingSessionService{

	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private SessionDao drivingSessionDao;
	@Override
	public DrivingSession saveDrivingSession(String candidatId, DrivingSession drivingSession) {
		Candidate candidate = candidateService.findByCin(candidatId);
		DrivingSession drivingSessionC = new DrivingSession();
		drivingSessionC.setCandidate(candidate);
		drivingSessionC.setDateDrivingSession(drivingSession.getDateDrivingSession());
		drivingSessionDao.save(drivingSessionC);
		return drivingSessionC;
	}

	@Override
	public Long deleteDrivingSession(Long id) {
		return drivingSessionDao.removeById(id);
	}

	@Override
	public Collection<DrivingSession> getDrivinSessionsOrderByDate(String candidatId) {
		Candidate candidate = candidateService.findByCin(candidatId);
		return drivingSessionDao.findByCandidatOrderByDateDrivingSessionAsc(candidate);
	}
}
