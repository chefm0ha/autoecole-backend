package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.dto.AddApplicationFileRequestDTO;
import com.springBoot.autoEcole.dto.ApplicationFileDTO;
import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.service.ApplicationFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
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

    @PutMapping("/updateTaxStampStatus/{id}")
    public ResponseEntity<String> updateTaxStampStatus(@PathVariable Long id, @RequestParam String taxStampStatus) {
        ApplicationFile existingFile = applicationFileService.findById(id);
        if (existingFile == null) {
            return ResponseEntity.notFound().build();
        }

        ApplicationFile updateRequest = new ApplicationFile();
        updateRequest.setTaxStamp(taxStampStatus);

        applicationFileService.updateApplicationFile(id, updateRequest);

        return ResponseEntity.ok("Tax stamp status updated successfully");
    }

    @PutMapping("/updateMedicalVisitStatus/{id}")
    public ResponseEntity<String> updateMedicalVisitStatus(@PathVariable Long id, @RequestParam String medicalVisitStatus) {
        ApplicationFile existingFile = applicationFileService.findById(id);
        if (existingFile == null) {
            return ResponseEntity.notFound().build();
        }

        ApplicationFile updateRequest = new ApplicationFile();
        updateRequest.setMedicalVisit(medicalVisitStatus);

        applicationFileService.updateApplicationFile(id, updateRequest);

        return ResponseEntity.ok("Medical visit status updated successfully");
    }

    @PutMapping("/cancelApplicationFile/{id}")
    public ResponseEntity<?> cancelApplicationFile(@PathVariable Long id) {
        try {
            applicationFileService.cancelApplicationFile(id);
            return ResponseEntity.ok("Application file cancelled successfully");
        } catch (IllegalStateException e) {
            // Business rule violations
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            // Application file not found
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Any other unexpected errors
            return ResponseEntity.status(500).body("Error cancelling application file: " + e.getMessage());
        }
    }

    @PutMapping("/updateTheoreticalHours/{id}")
    public ResponseEntity<String> updateTheoreticalHours(@PathVariable Long id, @RequestParam Double hours) {
        ApplicationFile existingFile = applicationFileService.findById(id);
        if (existingFile == null) {
            return ResponseEntity.notFound().build();
        }

        ApplicationFile updateRequest = new ApplicationFile();
        updateRequest.setTheoreticalHoursCompleted(hours);

        applicationFileService.updateApplicationFile(id, updateRequest);

        return ResponseEntity.ok("Theoretical hours updated successfully");
    }

    @PutMapping("/updatePracticalHours/{id}")
    public ResponseEntity<String> updatePracticalHours(@PathVariable Long id, @RequestParam Double hours) {
        ApplicationFile existingFile = applicationFileService.findById(id);
        if (existingFile == null) {
            return ResponseEntity.notFound().build();
        }

        ApplicationFile updateRequest = new ApplicationFile();
        updateRequest.setPracticalHoursCompleted(hours);

        applicationFileService.updateApplicationFile(id, updateRequest);

        return ResponseEntity.ok("Practical hours updated successfully");
    }
}
