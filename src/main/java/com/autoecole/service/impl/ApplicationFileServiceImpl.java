package com.autoecole.service.impl;

import com.autoecole.dto.AddApplicationFileRequestDTO;
import com.autoecole.dto.ApplicationFileCloseResponseDTO;
import com.autoecole.dto.ApplicationFileDTO;
import com.autoecole.enums.*;
import com.autoecole.exception.ApplicationFileException;
import com.autoecole.exception.NotFoundException;
import com.autoecole.model.*;
import com.autoecole.repository.ApplicationFileDao;
import com.autoecole.repository.PaymentDao;
import com.autoecole.repository.PaymentInstallmentDao;
import com.autoecole.service.ApplicationFileService;
import com.autoecole.service.CandidateService;
import com.autoecole.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ApplicationFileServiceImpl implements ApplicationFileService {

    private final ApplicationFileDao applicationFileDao;
    private final CandidateService candidateService;
    private final CategoryService categoryService;
    private final PaymentDao paymentDao;
    private final PaymentInstallmentDao paymentInstallmentDao;

    @Override
    public ApplicationFileDTO saveApplicationFile(String candidateCin, AddApplicationFileRequestDTO request) {
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

        // 9. Update payment with new paid amount and status
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
    }

    @Override
    public ApplicationFile updateApplicationFile(Long id, ApplicationFile applicationFile) {
        ApplicationFile existing = findById(id);

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
        return applicationFileDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Application file not found with ID: " + id));
    }

    @Override
    public List<ApplicationFileDTO> getApplicationFilesByCandidate(String candidateCin) {
        Candidate candidate = candidateService.findByCin(candidateCin);
        if (candidate == null) {
            throw new NotFoundException("Candidate not found with CIN: " + candidateCin);
        }

        List<ApplicationFile> applicationFiles = applicationFileDao.findByCandidate(candidate);
        return applicationFiles.stream()
                .map(ApplicationFileDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelApplicationFile(Long applicationFileId) {
        // 1. Find application file
        ApplicationFile applicationFile = findById(applicationFileId);

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
        cancelScheduledExams(applicationFile);

        // 6. Check if candidate has other active application files
        deactivateCandidateIfNoActiveFiles(applicationFile);
    }

    @Override
    public void updateTaxStampStatus(Long id, String taxStampStatus) {
        ApplicationFile applicationFile = findById(id);

        try {
            applicationFile.setTaxStamp(TaxStampStatus.valueOf(taxStampStatus));
            applicationFileDao.save(applicationFile);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid tax stamp status: " + taxStampStatus);
        }
    }

    @Override
    public void updateMedicalVisitStatus(Long id, String medicalVisitStatus) {
        ApplicationFile applicationFile = findById(id);

        try {
            applicationFile.setMedicalVisit(MedicalVisitStatus.valueOf(medicalVisitStatus));
            applicationFileDao.save(applicationFile);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid medical visit status: " + medicalVisitStatus);
        }
    }

    @Override
    public void updateTheoreticalHours(Long id, Double hours) {
        ApplicationFile applicationFile = findById(id);

        if (hours < 0) {
            throw new IllegalArgumentException("Theoretical hours cannot be negative");
        }

        applicationFile.setTheoreticalHoursCompleted(hours);
        applicationFileDao.save(applicationFile);
    }

    @Override
    public void updatePracticalHours(Long id, Double hours) {
        ApplicationFile applicationFile = findById(id);

        if (hours < 0) {
            throw new IllegalArgumentException("Practical hours cannot be negative");
        }

        applicationFile.setPracticalHoursCompleted(hours);
        applicationFileDao.save(applicationFile);
    }

    @Override
    public ApplicationFileCloseResponseDTO closeApplicationFile(Long applicationFileId) {
        // 1. Find application file
        ApplicationFile applicationFile = findById(applicationFileId);

        // 2. Check if already completed or cancelled
        if (applicationFile.getStatus() == ApplicationFileStatus.COMPLETED) {
            throw new ApplicationFileException(108, "Application file is already completed");
        }

        if (applicationFile.getStatus() == ApplicationFileStatus.CANCELLED) {
            throw new ApplicationFileException(109, "Application file is already cancelled");
        }

        // 3. Check if eligible for completion
        boolean isEligible = isEligibleForCompletion(applicationFileId);

        if (isEligible) {
            // Mark as COMPLETED
            applicationFile.setStatus(ApplicationFileStatus.COMPLETED);
            applicationFile.setIsActive(false);
            applicationFileDao.save(applicationFile);

            // Check if candidate has other active application files
            deactivateCandidateIfNoActiveFiles(applicationFile);

            return ApplicationFileCloseResponseDTO.completed();
        } else {
            // Mark as CANCELLED
            applicationFile.setStatus(ApplicationFileStatus.CANCELLED);
            applicationFile.setIsActive(false);

            // Cancel all scheduled exams
            cancelScheduledExams(applicationFile);

            applicationFileDao.save(applicationFile);

            // Check if candidate has other active application files
            deactivateCandidateIfNoActiveFiles(applicationFile);

            return ApplicationFileCloseResponseDTO.cancelled(
                    "Both theory and practical exams must be passed to complete the application file");
        }
    }

    @Override
    public boolean isEligibleForCompletion(Long applicationFileId) {
        ApplicationFile applicationFile = findById(applicationFileId);
        List<Exam> exams = applicationFile.getExams();

        // Check if both theory and practical exams are passed
        boolean theoryPassed = exams.stream()
                .anyMatch(e -> e.getExamType() == ExamType.THEORY && e.getStatus() == ExamStatus.PASSED);

        boolean practicalPassed = exams.stream()
                .anyMatch(e -> e.getExamType() == ExamType.PRACTICAL && e.getStatus() == ExamStatus.PASSED);

        return theoryPassed && practicalPassed;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void cancelScheduledExams(ApplicationFile applicationFile) {
        List<Exam> scheduledExams = applicationFile.getExams().stream()
                .filter(exam -> exam.getStatus() == ExamStatus.SCHEDULED)
                .collect(Collectors.toList());

        for (Exam exam : scheduledExams) {
            exam.setStatus(ExamStatus.FAILED);
        }
    }

    private void deactivateCandidateIfNoActiveFiles(ApplicationFile applicationFile) {
        Candidate candidate = applicationFile.getCandidate();
        boolean hasOtherActiveFiles = candidate.getApplicationFiles().stream()
                .anyMatch(af -> af.getIsActive() && !af.getId().equals(applicationFile.getId()));

        if (!hasOtherActiveFiles) {
            candidate.setIsActive(false);
            candidateService.saveCandidate(candidate);
        }
    }
}