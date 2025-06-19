package com.springBoot.autoEcole.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.repository.ISessionDao;
import com.springBoot.autoEcole.service.CandidatService;
import com.springBoot.autoEcole.service.DrivingSessionService;

@Service
@Transactional
public class DrivingSessionServiceImpl implements DrivingSessionService{

	@Autowired
	private CandidatService candidatService;
	
	@Autowired
	private ISessionDao drivingSessionDao;
	@Override
	public DrivingSession saveDrivingSession(String candidatId, DrivingSession drivingSession) {
		Candidate candidate = candidatService.findById(candidatId);
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
		Candidate candidate = candidatService.findById(candidatId);
		return drivingSessionDao.findByCandidatOrderByDateDrivingSessionAsc(candidate);
	}
}
