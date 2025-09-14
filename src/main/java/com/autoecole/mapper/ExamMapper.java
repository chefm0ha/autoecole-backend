package com.autoecole.mapper;

import org.springframework.stereotype.Component;
import com.autoecole.model.Exam;
import com.autoecole.model.ApplicationFile;
import com.autoecole.model.Vehicle;

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
        target.setVehicle(source.getVehicle()); // Copy vehicle if set

        return target;
    }

    public Exam toEntity(Exam source, ApplicationFile applicationFile, Vehicle vehicle) {
        if (source == null || applicationFile == null) {
            return null;
        }

        Exam target = new Exam();
        target.setAttemptNumber(source.getAttemptNumber());
        target.setDate(source.getDate());
        target.setExamType(source.getExamType());
        target.setStatus(source.getStatus());
        target.setApplicationFile(applicationFile);
        target.setVehicle(vehicle); // Set specific vehicle

        return target;
    }
}