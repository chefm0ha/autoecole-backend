package com.springBoot.autoEcole.service.impl;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.mapper.SessionMapper;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Session;
import com.springBoot.autoEcole.repository.SessionDao;
import com.springBoot.autoEcole.service.CandidateService;
import com.springBoot.autoEcole.service.SessionService;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private SessionMapper sessionMapper;

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