package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.repository.ApplicationFileDao;
import com.springBoot.autoEcole.service.ApplicationFileService;
import com.springBoot.autoEcole.service.CandidateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;

@Service
@Transactional
public class ApplicationFileServiceImpl implements ApplicationFileService {

    @Autowired
    private ApplicationFileDao applicationFileDao;

    @Autowired
    private CandidateService candidateService;

    @Override
    public ApplicationFile saveApplicationFile(String candidateCin, ApplicationFile applicationFile) {
        Candidate candidate = candidateService.findByCin(candidateCin);
        if (candidate == null) {
            throw new EntityNotFoundException("Candidate not found with CIN: " + candidateCin);
        }

        // Check if an active application file already exists for this category
        if (applicationFile.getCategory() != null) {
            ApplicationFile existingFile = applicationFileDao.findByCandidateAndCategory(candidate, applicationFile.getCategory());
            if (existingFile != null && existingFile.getIsActive()) {
                throw new IllegalStateException("An active application file already exists for category: " + applicationFile.getCategory().getCode());
            }
        }

        applicationFile.setCandidate(candidate);
        applicationFile.setStartingDate(LocalDate.now());
        applicationFile.setIsActive(true);

        return applicationFileDao.save(applicationFile);
    }

    @Override
    public ApplicationFile updateApplicationFile(Long id, ApplicationFile applicationFile) {
        ApplicationFile existing = findById(id);
        if (existing == null) {
            throw new EntityNotFoundException("Application file not found with ID: " + id);
        }

        // Update fields
        if (applicationFile.getPracticalHoursCompleted() != null) {
            existing.setPracticalHoursCompleted(applicationFile.getPracticalHoursCompleted());
        }
        if (applicationFile.getTheoreticalHoursCompleted() != null) {
            existing.setTheoreticalHoursCompleted(applicationFile.getTheoreticalHoursCompleted());
        }
        if (applicationFile.getStatus() != null) {
            existing.setStatus(applicationFile.getStatus());
        }
        if (applicationFile.getTaxStamp() != null) {
            existing.setTaxStamp(applicationFile.getTaxStamp());
        }
        if (applicationFile.getMedicalVisit() != null) {
            existing.setMedicalVisit(applicationFile.getMedicalVisit());
        }
        if (applicationFile.getIsActive() != null) {
            existing.setIsActive(applicationFile.getIsActive());
        }

        return applicationFileDao.save(existing);
    }

    @Override
    public Long deleteApplicationFile(Long id) {
        return applicationFileDao.removeById(id);
    }

    @Override
    public ApplicationFile findById(Long id) {
        return applicationFileDao.findById(id).orElse(null);
    }
}