package com.autoecole.service;

import com.autoecole.dto.request.AddApplicationFileRequestDTO;
import com.autoecole.dto.response.ApplicationFileDTO;
import com.autoecole.model.ApplicationFile;

import java.util.List;

public interface ApplicationFileService {

    ApplicationFileDTO saveApplicationFile(String candidateCin, AddApplicationFileRequestDTO request);
    ApplicationFile updateApplicationFile(Long id, ApplicationFile applicationFile);
    Long deleteApplicationFile(Long id);
    ApplicationFile findById(Long id);
    List<ApplicationFileDTO> getApplicationFilesByCandidate(String candidateCin);
    void cancelApplicationFile(Long applicationFileId);

    // Specific update methods for individual fields
    void updateTaxStampStatus(Long id, String taxStampStatus);
    void updateMedicalVisitStatus(Long id, String medicalVisitStatus);
    void updateTheoreticalHours(Long id, Double hours);
    void updatePracticalHours(Long id, Double hours);
}