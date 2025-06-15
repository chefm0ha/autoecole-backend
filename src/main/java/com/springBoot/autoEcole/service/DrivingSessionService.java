package com.springBoot.autoEcole.service;

import java.util.Collection;

import com.springBoot.autoEcole.model.DrivingSession;

public interface DrivingSessionService {

	DrivingSession saveDrivingSession(String candidatId, DrivingSession drivingSession);

	Long deleteDrivingSession(Long id);

	Collection<DrivingSession> getDrivinSessionsOrderByDate(String candidatId);

}
