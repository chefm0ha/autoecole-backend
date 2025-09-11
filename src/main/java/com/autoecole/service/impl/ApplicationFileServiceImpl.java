package com.autoecole.service.impl;

import com.autoecole.dto.request.AddApplicationFileRequestDTO;
import com.autoecole.dto.response.ApplicationFileCloseResponseDTO;
import com.autoecole.dto.response.ApplicationFileDTO;
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

import java.util.List;

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

        Candidate candidate = candidateService.findByCin(candidateCin);

        Category category = categoryService.findByCode(request.getCategoryCode());

        // Check for an active application file for this category
        ApplicationFile existingActiveFile = applicationFileDao.findByCandidateAndCategory(candidate, category);
        if (existingActiveFile != null && existingActiveFile.getIsActive() &&
                existingActiveFile.getStatus() == ApplicationFileStatus.IN_PROGRESS) {
            throw new ApplicationFileException(100, "Cannot add application file: An active application file is already in progress for this category");
        }

        // Check for a completed application file for this category
        if (existingActiveFile != null && existingActiveFile.getStatus() == ApplicationFileStatus.COMPLETED) {
            throw new ApplicationFileException(101, "Cannot add application file: A completed application file already exists for this category");
        }

        // Generate file number
        String fileNumber = "test-" + candidateCin + "-test";

        // Create a new application file
        ApplicationFile savedApplicationFile = applicationFileDao.save(
                ApplicationFile.createNew(candidate, category, fileNumber)
        );

        // Create payment record
        Payment savedPayment = paymentDao.save(
                Payment.createNew(savedApplicationFile, request.getTotalAmount())
        );

        // Create initial payment installment
        paymentInstallmentDao.save(
                PaymentInstallment.createInitial(savedPayment, request.getInitialAmount())
        );

        // Update payment with new paid amount and status
        savedPayment.setPaidAmount(request.getInitialAmount());
        if (request.getInitialAmount() >= savedPayment.getTotalAmount()) {
            savedPayment.setStatus(PaymentStatus.COMPLETED);
        } else {
            savedPayment.setStatus(PaymentStatus.PENDING);
        }
        paymentDao.save(savedPayment);

        // Activate the candidate
        if(Boolean.FALSE.equals(candidate.getIsActive())) {
            candidate.setIsActive(true);
            candidateService.updateCandidate(candidate.getCin(), candidate);
        }

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
                .toList();
    }

    @Override
    public void cancelApplicationFile(Long applicationFileId) {
        // 1. Find an application file
        ApplicationFile applicationFile = findById(applicationFileId);

        // 2. Check if already completed (cannot cancel)
        if (applicationFile.getStatus() == ApplicationFileStatus.COMPLETED) {
            throw new ApplicationFileException(102, "Cannot cancel a completed application file");
        }

        // 3. Check if already canceled
        if (applicationFile.getStatus() == ApplicationFileStatus.CANCELLED) {
            throw new ApplicationFileException(103, "Application file is already cancelled");
        }

        // 4. Update application file status
        applicationFile.setStatus(ApplicationFileStatus.CANCELLED);
        applicationFile.setIsActive(false);
        applicationFileDao.save(applicationFile);

        // 5. Cancel all scheduled exams
        cancelScheduledExams(applicationFile);

        // 6. Check if a candidate has other active application files
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

    // ==================== PRIVATE HELPER METHODS ====================

    private void cancelScheduledExams(ApplicationFile applicationFile) {
        List<Exam> scheduledExams = applicationFile.getExams().stream()
                .filter(exam -> exam.getStatus() == ExamStatus.SCHEDULED)
                .toList();

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
            candidateService.updateCandidate(candidate.getCin(), candidate);
        }
    }
}