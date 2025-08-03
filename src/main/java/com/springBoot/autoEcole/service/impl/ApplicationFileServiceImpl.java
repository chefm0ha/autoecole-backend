package com.springBoot.autoEcole.service.impl;

import com.springBoot.autoEcole.dto.AddApplicationFileRequestDTO;
import com.springBoot.autoEcole.dto.ApplicationFileDTO;
import com.springBoot.autoEcole.enums.*;
import com.springBoot.autoEcole.model.*;
import com.springBoot.autoEcole.repository.ApplicationFileDao;
import com.springBoot.autoEcole.repository.PaymentDao;
import com.springBoot.autoEcole.repository.PaymentInstallmentDao;
import com.springBoot.autoEcole.service.ApplicationFileService;
import com.springBoot.autoEcole.service.CandidateService;
import com.springBoot.autoEcole.service.CategoryService;
import lombok.Getter;
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
            // 1. Validate candidate exists
            Candidate candidate = candidateService.findByCin(candidateCin);
            if (candidate == null) {
                throw new ApplicationFileException(100, "Candidate not found");
            }

            // 2. Validate category exists
            Category category = categoryService.findByCode(request.getCategoryCode());
            if (category == null) {
                throw new ApplicationFileException(101, "Category not found");
            }

            // 3. Check for active application file for this category
            ApplicationFile existingActiveFile = applicationFileDao.findByCandidateAndCategory(candidate, category);
            if (existingActiveFile != null && existingActiveFile.getIsActive() && 
                existingActiveFile.getStatus() == ApplicationFileStatus.IN_PROGRESS) {
                throw new ApplicationFileException(102, "Cannot add application file: An active application file is already in progress for this category");
            }

            // 4. Check for completed application file for this category
            if (existingActiveFile != null && existingActiveFile.getStatus() == ApplicationFileStatus.COMPLETED) {
                throw new ApplicationFileException(103, "Cannot add application file: A completed application file already exists for this category");
            }

            // 5. Generate file number
            String fileNumber = "test-" + candidateCin + "-test";

            // 6. Create new application file
            ApplicationFile newApplicationFile = ApplicationFile.builder()
                .candidate(candidate)
                .category(category)
                .practicalHoursCompleted(0.0)
                .theoreticalHoursCompleted(0.0)
                .isActive(true)
                .startingDate(LocalDate.now())
                .status(ApplicationFileStatus.IN_PROGRESS)
                .fileNumber(fileNumber)
                .taxStamp(TaxStampStatus.NOT_PAID)
                .medicalVisit(MedicalVisitStatus.NOT_REQUESTED)
                .build();

            ApplicationFile savedApplicationFile = applicationFileDao.save(newApplicationFile);

            // 7. Create payment record
            Payment payment = Payment.builder()
                .applicationFile(savedApplicationFile)
                .paidAmount(0)
                .status(PaymentStatus.PENDING)
                .totalAmount(request.getTotalAmount())
                .build();

            Payment savedPayment = paymentDao.save(payment);

            // 8. Create initial payment installment
            PaymentInstallment initialInstallment = PaymentInstallment.builder()
                .payment(savedPayment)
                .amount(request.getInitialAmount())
                .date(LocalDate.now())
                .installmentNumber(1)
                .build();

            paymentInstallmentDao.save(initialInstallment);

            // 9. Update payment with new paid amount and status (manual calculation for the initial installment)
            savedPayment.setPaidAmount(request.getInitialAmount());
            if (request.getInitialAmount() >= savedPayment.getTotalAmount()) {
                savedPayment.setStatus(PaymentStatus.COMPLETED);
            } else {
                savedPayment.setStatus(PaymentStatus.PENDING);
            }
            paymentDao.save(savedPayment);

            // 10. Activate the candidate
            candidate.setIsActive(true);
            candidateService.saveCandidate(candidate);

            return ApplicationFileDTO.fromEntity(savedApplicationFile);

        } catch (ApplicationFileException e) {
            throw e;
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
            // 1. Find application file
            ApplicationFile applicationFile = findById(applicationFileId);
            if (applicationFile == null) {
                throw new ApplicationFileException(104, "Application file not found");
            }

            // 2. Check if already completed (cannot cancel)
            if (applicationFile.getStatus() == ApplicationFileStatus.COMPLETED) {
                throw new ApplicationFileException(105, "Cannot cancel a completed application file");
            }

            // 3. Check if already cancelled
            if (applicationFile.getStatus() == ApplicationFileStatus.CANCELLED) {
                throw new ApplicationFileException(106, "Application file is already cancelled");
            }

            // 4. Update application file status
            applicationFile.setStatus(ApplicationFileStatus.CANCELLED);
            applicationFile.setIsActive(false);
            applicationFileDao.save(applicationFile);

            // 5. Cancel all scheduled exams
            List<Exam> scheduledExams = applicationFile.getExams().stream()
                .filter(exam -> exam.getStatus() == ExamStatus.SCHEDULED)
                .collect(Collectors.toList());
            
            for (Exam exam : scheduledExams) {
                exam.setStatus(ExamStatus.FAILED);
            }

            // 6. Check if candidate has other active application files
            Candidate candidate = applicationFile.getCandidate();
            boolean hasOtherActiveFiles = candidate.getApplicationFiles().stream()
                .anyMatch(af -> af.getIsActive() && !af.getId().equals(applicationFileId));

            if (!hasOtherActiveFiles) {
                candidate.setIsActive(false);
                candidateService.saveCandidate(candidate);
            }

        } catch (ApplicationFileException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error cancelling application file: " + e.getMessage());
        }
    }

    @Override
    public void updateTaxStampStatus(Long id, String taxStampStatus) {
        ApplicationFile applicationFile = findById(id);
        if (applicationFile == null) {
            throw new EntityNotFoundException("Application file not found with ID: " + id);
        }
        
        applicationFile.setTaxStamp(TaxStampStatus.valueOf(taxStampStatus));
        applicationFileDao.save(applicationFile);
    }

    @Override
    public void updateMedicalVisitStatus(Long id, String medicalVisitStatus) {
        ApplicationFile applicationFile = findById(id);
        if (applicationFile == null) {
            throw new EntityNotFoundException("Application file not found with ID: " + id);
        }
        
        applicationFile.setMedicalVisit(MedicalVisitStatus.valueOf(medicalVisitStatus));
        applicationFileDao.save(applicationFile);
    }

    @Override
    public void updateTheoreticalHours(Long id, Double hours) {
        ApplicationFile applicationFile = findById(id);
        if (applicationFile == null) {
            throw new EntityNotFoundException("Application file not found with ID: " + id);
        }
        
        if (hours < 0) {
            throw new IllegalArgumentException("Theoretical hours cannot be negative");
        }
        
        applicationFile.setTheoreticalHoursCompleted(hours);
        applicationFileDao.save(applicationFile);
    }

    @Override
    public void updatePracticalHours(Long id, Double hours) {
        ApplicationFile applicationFile = findById(id);
        if (applicationFile == null) {
            throw new EntityNotFoundException("Application file not found with ID: " + id);
        }
        
        if (hours < 0) {
            throw new IllegalArgumentException("Practical hours cannot be negative");
        }
        
        applicationFile.setPracticalHoursCompleted(hours);
        applicationFileDao.save(applicationFile);
    }

    @Override
    public void closeApplicationFile(Long applicationFileId) {
        try {
            // 1. Find application file
            ApplicationFile applicationFile = findById(applicationFileId);
            if (applicationFile == null) {
                throw new ApplicationFileException(104, "Application file not found");
            }

            // 2. Check if already completed or cancelled
            if (applicationFile.getStatus() == ApplicationFileStatus.COMPLETED) {
                throw new ApplicationFileException(108, "Application file is already completed");
            }

            if (applicationFile.getStatus() == ApplicationFileStatus.CANCELLED) {
                throw new ApplicationFileException(109, "Application file is already cancelled");
            }

            // 3. Check if eligible for completion
            if (isEligibleForCompletion(applicationFileId)) {
                // Mark as COMPLETED
                applicationFile.setStatus(ApplicationFileStatus.COMPLETED);
                applicationFile.setIsActive(false);
            } else {
                // Mark as CANCELLED
                applicationFile.setStatus(ApplicationFileStatus.CANCELLED);
                applicationFile.setIsActive(false);

                // Cancel all scheduled exams
                List<Exam> scheduledExams = applicationFile.getExams().stream()
                        .filter(exam -> exam.getStatus() == ExamStatus.SCHEDULED)
                        .collect(Collectors.toList());

                for (Exam exam : scheduledExams) {
                    exam.setStatus(ExamStatus.FAILED);
                }
            }

            applicationFileDao.save(applicationFile);

            // 4. Check if candidate has other active application files
            Candidate candidate = applicationFile.getCandidate();
            boolean hasOtherActiveFiles = candidate.getApplicationFiles().stream()
                    .anyMatch(af -> af.getIsActive() && !af.getId().equals(applicationFileId));

            if (!hasOtherActiveFiles) {
                candidate.setIsActive(false);
                candidateService.saveCandidate(candidate);
            }

        } catch (ApplicationFileException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error closing application file: " + e.getMessage());
        }
    }

    @Override
    public boolean isEligibleForCompletion(Long applicationFileId) {
        ApplicationFile applicationFile = findById(applicationFileId);
        if (applicationFile == null) {
            return false;
        }

        List<Exam> exams = applicationFile.getExams();

        // Check if both theory and practical exams are passed
        boolean theoryPassed = exams.stream()
                .anyMatch(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.PASSED);

        boolean practicalPassed = exams.stream()
                .anyMatch(e -> e.getExamType() == ExamType.PRACTICAL && e.getStatus() == ExamStatus.PASSED);

        return theoryPassed && practicalPassed;
    }

    /**
     * Updates payment status and paid amount after a new installment is added
     */
    private void updatePaymentAfterInstallment(Payment payment) {
        // Calculate total paid amount from all installments
        Integer totalPaidAmount = 0;
        if (payment.getPaymentInstallments() != null) {
            totalPaidAmount = payment.getPaymentInstallments().stream()
                .mapToInt(PaymentInstallment::getAmount)
                .sum();
        }

        // Update payment
        payment.setPaidAmount(totalPaidAmount);
        
        if (totalPaidAmount >= payment.getTotalAmount()) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else if (totalPaidAmount > 0) {
            payment.setStatus(PaymentStatus.PENDING);
        }

        paymentDao.save(payment);
    }

    // Custom exception class for application file errors
    @Getter
    public static class ApplicationFileException extends RuntimeException {
        private final int errorCode;

        public ApplicationFileException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
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
        if (causeMessage.contains("Cannot add more exams: application file has already failed due to multiple failures")) {
            return new ApplicationFileError(107, "Cannot add more exams: application file has already failed due to multiple failures");
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
    @Getter
    private static class ApplicationFileError {
        private final int code;
        private final String message;

        public ApplicationFileError(int code, String message) {
            this.code = code;
            this.message = message;
        }

    }
}