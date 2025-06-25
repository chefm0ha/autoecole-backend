package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.dto.AddApplicationFileRequestDTO;
import com.springBoot.autoEcole.dto.ApplicationFileDTO;
import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.service.ApplicationFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applicationFile")
@CrossOrigin
public class ApplicationFileFacade {

    @Autowired
    private ApplicationFileService applicationFileService;

    @PostMapping("/saveApplicationFile/{candidateCin}")
    public ApplicationFileDTO saveApplicationFile(@PathVariable String candidateCin, @RequestBody AddApplicationFileRequestDTO request) {
        return applicationFileService.saveApplicationFile(candidateCin, request);
    }

    @PutMapping("/updateApplicationFile/{id}")
    public ApplicationFile updateApplicationFile(@PathVariable Long id, @RequestBody ApplicationFile applicationFile) {
        return applicationFileService.updateApplicationFile(id, applicationFile);
    }

    @GetMapping("/getApplicationFile/{id}")
    public ApplicationFile getApplicationFileById(@PathVariable Long id) {
        return applicationFileService.findById(id);
    }

    @DeleteMapping("/deleteApplicationFile/{id}")
    public Long deleteApplicationFile(@PathVariable Long id) {
        return applicationFileService.deleteApplicationFile(id);
    }

    @GetMapping("/getApplicationFileByCandidate/{candidateCin}")
    public List<ApplicationFileDTO> getApplicationFileByCandidate(@PathVariable String candidateCin) {
        return applicationFileService.getApplicationFilesByCandidate(candidateCin);
    }
}
