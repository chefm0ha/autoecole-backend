package com.autoecole.service;

import java.util.Collection;
import com.autoecole.model.Session;

public interface SessionService {
    Collection<Session> findAllSessions();
    Session saveSession(String candidateCin, Session session);
    Session findById(Long id);
    Long deleteSession(Long id);
    Collection<Session> getSessionsOrderByDate(String candidateCin);
}