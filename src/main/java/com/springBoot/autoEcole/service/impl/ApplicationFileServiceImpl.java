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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Override
    public ApplicationFileDTO saveApplicationFile(String candidateCin, AddApplicationFileRequestDTO request) {
        // 1. Find candidate and category
        Candidate candidate = candidateService.findByCin(candidateCin);
        if (candidate == null) {
            throw new EntityNotFoundException("Candidate not found with CIN: " + candidateCin);
        }

        Category category = categoryService.findByCode(request.getCategoryCode());
        if (category == null) {
            throw new EntityNotFoundException("Category not found with code: " + request.getCategoryCode());
        }

        // Check if an active application file already exists for this category
        ApplicationFile existingFile = applicationFileDao.findByCandidateAndCategory(candidate, category);
        if (existingFile != null && existingFile.getIsActive()) {
            throw new IllegalStateException("An active application file already exists for category: " + request.getCategoryCode());
        }

        // 2. Create ApplicationFile (database defaults will handle most fields)
        ApplicationFile applicationFile = ApplicationFile.builder()
                .practicalHoursCompleted(0.0)
                .theoreticalHoursCompleted(0.0)
                .isActive(true)
                .startingDate(LocalDate.now())
                .status("IN_PROGRESS")
                .fileNumber("test-" + candidateCin + "-test")
                .taxStamp("NOT_PAID")
                .medicalVisit("NOT_REQUESTED")
                .candidate(candidate)
                .category(category)
                .build();

        ApplicationFile savedApplicationFile = applicationFileDao.save(applicationFile);

        // 3. Create Payment (database defaults will handle paid_amount and status)
        Payment payment = Payment.builder()
                .paidAmount(0)
                .status("PENDING")
                .totalAmount(request.getTotalAmount())
                .applicationFile(savedApplicationFile)
                .build();

        Payment savedPayment = paymentDao.save(payment);
        System.out.println(request.getTotalAmount());
        // 4. Create Initial PaymentInstallment
        PaymentInstallment initialInstallment = PaymentInstallment.builder()
                .amount(request.getInitialAmount())
                .date(LocalDate.now())
                .installmentNumber(1)
                .payment(savedPayment)
                .build();

        paymentInstallmentDao.save(initialInstallment);

        // Note: The trigger will automatically update payment paidAmount and status

        return ApplicationFileDTO.fromEntity(savedApplicationFile);
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
}