package com.autoecole.mapper;

import org.springframework.stereotype.Component;
import com.autoecole.model.Exam;
import com.autoecole.model.ApplicationFile;

@Component
public class ExamMapper {

    public Exam toEntity(Exam source, ApplicationFile applicationFile) {
        if (source == null || applicationFile == null) {
            return null;
        }

        Exam target = new Exam();
        target.setAttemptNumber(source.getAttemptNumber());
        target.setDate(source.getDate());
        target.setExamType(source.getExamType());
        target.setStatus(source.getStatus());
        target.setApplicationFile(applicationFile);

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
        // Note: applicationFile is ignored in update
    }
}