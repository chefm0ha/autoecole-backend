package com.springBoot.autoEcole.service;

import com.springBoot.autoEcole.model.ApplicationFile;

public interface ApplicationFileService {
    ApplicationFile saveApplicationFile(String candidateCin, ApplicationFile applicationFile);
    ApplicationFile updateApplicationFile(Long id, ApplicationFile applicationFile);
    Long deleteApplicationFile(Long id);
    ApplicationFile findById(Long id);
}
