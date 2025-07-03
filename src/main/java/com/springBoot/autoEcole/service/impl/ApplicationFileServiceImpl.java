package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.dto.AddApplicationFileRequestDTO;
import com.springBoot.autoEcole.dto.ApplicationFileDTO;
import com.springBoot.autoEcole.model.*;
import com.springBoot.autoEcole.repository.ApplicationFileDao;
import com.springBoot.autoEcole.repository.PaymentDao;
import com.springBoot.autoEcole.repository.PaymentInstallmentDao;
import com.springBoot.autoEcole.service.ApplicationFileService;
import com.springBoot.autoEcole.service.CandidateService;
import com.springBoot.autoEcole.service.CategoryService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationFileServiceImpl implements ApplicationFileService {

    @Autowired
    private ApplicationFileDao applicationFileDao;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private PaymentInstallmentDao paymentInstallmentDao;

    @Autowired
    private EntityManager entityManager;

    @Override
    public ApplicationFileDTO saveApplicationFile(String candidateCin, AddApplicationFileRequestDTO request) {
        try {
            // Call stored procedure to validate and save application file
            applicationFileDao.saveApplicationFileWithValidation(
                    candidateCin,
                    request.getCategoryCode(),
                    request.getTotalAmount(),
                    request.getInitialAmount()
            );

            // Get the ID of the newly created application file
            Long applicationFileId = applicationFileDao.getLastApplicationFileId();

            // Clear Hibernate cache to get fresh data
            entityManager.clear();

            // Retrieve and return the created application file
            ApplicationFile savedApplicationFile = applicationFileDao.findById(applicationFileId).orElse(null);

            if (savedApplicationFile == null) {
                throw new RuntimeException("Failed to retrieve created application file");
            }

            return ApplicationFileDTO.fromEntity(savedApplicationFile);

        } catch (DataAccessException e) {
            ApplicationFileError error = extractErrorMessage(e);
            throw new ApplicationFileException(error.getCode(), error.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error saving application file: " + e.getMessage());
        }
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

    @Override
    public List<ApplicationFileDTO> getApplicationFilesByCandidate(String candidateCin) {
        Candidate candidate = candidateService.findByCin(candidateCin);
        if (candidate == null) {
            throw new EntityNotFoundException("Candidate not found with CIN: " + candidateCin);
        }

        List<ApplicationFile> applicationFiles = applicationFileDao.findByCandidate(candidate);
        return applicationFiles.stream()
                .map(ApplicationFileDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelApplicationFile(Long applicationFileId) {
        try {
            applicationFileDao.cancelApplicationFile(applicationFileId);
        } catch (DataAccessException e) {
            ApplicationFileError error = extractErrorMessage(e);
            throw new ApplicationFileException(error.getCode(), error.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error cancelling application file: " + e.getMessage());
        }
    }

    // Custom exception class for application file errors
    public static class ApplicationFileException extends RuntimeException {
        private final int errorCode;

        public ApplicationFileException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    private ApplicationFileError extractErrorMessage(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return new ApplicationFileError(999, "Database error occurred");
        }

        // Try to extract the actual error message from nested exceptions
        Throwable cause = e;
        while (cause != null) {
            String causeMessage = cause.getMessage();
            if (causeMessage != null) {
                ApplicationFileError extractedError = mapKnownErrorMessage(causeMessage);
                if (extractedError != null) {
                    return extractedError;
                }
            }
            cause = cause.getCause();
        }

        return new ApplicationFileError(999, message);
    }

    private ApplicationFileError mapKnownErrorMessage(String causeMessage) {
        // Map known database error messages to error codes and messages
        if (causeMessage.contains("Candidate not found")) {
            return new ApplicationFileError(100, "Candidate not found");
        }
        if (causeMessage.contains("Category not found")) {
            return new ApplicationFileError(101, "Category not found");
        }
        if (causeMessage.contains("Cannot add application file: An active application file is already in progress for this category")) {
            return new ApplicationFileError(102, "Cannot add application file: An active application file is already in progress for this category");
        }
        if (causeMessage.contains("Cannot add application file: A completed application file already exists for this category")) {
            return new ApplicationFileError(103, "Cannot add application file: A completed application file already exists for this category");
        }
        if (causeMessage.contains("Application file not found")) {
            return new ApplicationFileError(104, "Application file not found");
        }
        if (causeMessage.contains("Cannot cancel a completed application file")) {
            return new ApplicationFileError(105, "Cannot cancel a completed application file");
        }
        if (causeMessage.contains("Application file is already cancelled")) {
            return new ApplicationFileError(106, "Application file is already cancelled");
        }

        return null; // No known mapping found
    }

    // Inner class for error handling
    private static class ApplicationFileError {
        private final int code;
        private final String message;

        public ApplicationFileError(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}