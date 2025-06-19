package com.springBoot.autoEcole.mapper;

import org.springframework.stereotype.Component;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.model.Candidate;

@Component
public class ExamMapper {

    public Exam toEntity(Exam source, Candidate candidate) {
        if (source == null || candidate == null) {
            return null;
        }

        Exam target = new Exam();
        target.setAttemptNumber(source.getAttemptNumber());
        target.setDate(source.getDate());
        target.setExamType(source.getExamType());
        target.setStatus(source.getStatus());
        target.setCandidate(candidate);

        return target;
    }

    public void updateEntity(Exam target, Exam source) {
        if (source == null || target == null) {
            return;
        }

        target.setAttemptNumber(source.getAttemptNumber());
        target.setDate(source.getDate());
        target.setExamType(source.getExamType());
        target.setStatus(source.getStatus());
        // Note: candidate is ignored in update
    }
}