package com.autoecole.mapper;

import org.springframework.stereotype.Component;
import com.autoecole.model.Session;
import com.autoecole.model.Candidate;

@Component
public class SessionMapper {

    public Session toEntity(Session source, Candidate candidate) {
        if (source == null || candidate == null) {
            return null;
        }

        Session target = new Session();
        target.setDateSession(source.getDateSession());
        target.setDuration(source.getDuration());
        target.setStatus(source.getStatus());
        target.setSessionType(source.getSessionType());
        target.setCandidate(candidate);

        return target;
    }

    public void updateEntity(Session target, Session source) {
        if (source == null || target == null) {
            return;
        }

        target.setDateSession(source.getDateSession());
        target.setDuration(source.getDuration());
        target.setStatus(source.getStatus());
        target.setSessionType(source.getSessionType());
        // Note: candidate is ignored in update
    }
}