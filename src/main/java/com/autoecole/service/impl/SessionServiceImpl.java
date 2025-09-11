package com.autoecole.service.impl;

import java.util.Collection;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.mapper.SessionMapper;
import com.autoecole.model.Candidate;
import com.autoecole.model.Session;
import com.autoecole.repository.SessionDao;
import com.autoecole.service.CandidateService;
import com.autoecole.service.SessionService;

@Service
@Transactional
@AllArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final CandidateService candidateService;
    private final SessionDao sessionDao;
    private final SessionMapper sessionMapper;

    @Override
    public Collection<Session> findAllSessions() {
        return (Collection<Session>) sessionDao.findAll();
    }

    @Override
    public Session saveSession(String candidateCin, Session session) {
        Candidate candidate = candidateService.findByCin(candidateCin);
        Session sessionToSave = sessionMapper.toEntity(session, candidate);
        return sessionDao.save(sessionToSave);
    }

    @Override
    public Session findById(Long id) {
        return sessionDao.findById(id).orElse(null);
    }

    @Override
    public Long deleteSession(Long id) {
        return sessionDao.removeById(id);
    }

    @Override
    public Collection<Session> getSessionsOrderByDate(String candidateCin) {
        Candidate candidate = candidateService.findByCin(candidateCin);
        return sessionDao.findByCandidateOrderByDateSessionAsc(candidate);
    }
}