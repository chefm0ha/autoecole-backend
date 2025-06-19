package com.springBoot.autoEcole.service;

import java.util.Collection;

public interface DrivingSessionService {

	DrivingSession saveDrivingSession(String candidatId, DrivingSession drivingSession);

	Long deleteDrivingSession(Long id);

	Collection<DrivingSession> getDrivinSessionsOrderByDate(String candidatId);

}
