package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.service.ApplicationFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applicationFile")
@CrossOrigin
public class ApplicationFileFacade {

    @Autowired
    private ApplicationFileService applicationFileService;

    @PostMapping("/saveApplicationFile/{candidateCin}")
    public ApplicationFile saveApplicationFile(@PathVariable String candidateCin, @RequestBody ApplicationFile applicationFile) {
        return applicationFileService.saveApplicationFile(candidateCin, applicationFile);
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
}
