package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.dto.AddApplicationFileRequestDTO;
import com.springBoot.autoEcole.dto.ApplicationFileDTO;
import com.springBoot.autoEcole.model.ApplicationFile;

import java.util.List;

public interface ApplicationFileService {
    ApplicationFileDTO saveApplicationFile(String candidateCin, AddApplicationFileRequestDTO request);
    ApplicationFile updateApplicationFile(Long id, ApplicationFile applicationFile);
    Long deleteApplicationFile(Long id);
    ApplicationFile findById(Long id);
    List<ApplicationFileDTO> getApplicationFilesByCandidate(String candidateCin);
    void cancelApplicationFile(Long applicationFileId);
}
