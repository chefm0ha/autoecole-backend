package com.autoecole.controller;

import com.autoecole.dto.AddApplicationFileRequestDTO;
import com.autoecole.dto.ApplicationFileDTO;
import com.autoecole.dto.ApplicationFileCloseResponseDTO;
import com.autoecole.model.ApplicationFile;
import com.autoecole.service.ApplicationFileService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applicationFile")
@CrossOrigin
@AllArgsConstructor
public class ApplicationFileFacade {

    private final ApplicationFileService applicationFileService;

    @PostMapping("/saveApplicationFile/{candidateCin}")
    public ApplicationFileDTO saveApplicationFile(
            @PathVariable String candidateCin,
            @RequestBody AddApplicationFileRequestDTO request) {
        return applicationFileService.saveApplicationFile(candidateCin, request);
    }

    @PutMapping("/updateApplicationFile/{id}")
    public ApplicationFile updateApplicationFile(
            @PathVariable Long id,
            @RequestBody ApplicationFile applicationFile) {
        return applicationFileService.updateApplicationFile(id, applicationFile);
    }

    @GetMapping("/getApplicationFile/{id}")
    public ApplicationFile getApplicationFileById(@PathVariable Long id) {
        return applicationFileService.findById(id);
    }

    @DeleteMapping("/deleteApplicationFile/{id}")
    public ResponseEntity<String> deleteApplicationFile(@PathVariable Long id) {
        applicationFileService.deleteApplicationFile(id);
        return ResponseEntity.ok("Application file deleted successfully");
    }

    @GetMapping("/getApplicationFileByCandidate/{candidateCin}")
    public List<ApplicationFileDTO> getApplicationFileByCandidate(@PathVariable String candidateCin) {
        return applicationFileService.getApplicationFilesByCandidate(candidateCin);
    }

    @PutMapping("/updateTaxStampStatus/{id}")
    public ResponseEntity<String> updateTaxStampStatus(
            @PathVariable Long id,
            @RequestParam String taxStampStatus) {
        applicationFileService.updateTaxStampStatus(id, taxStampStatus);
        return ResponseEntity.ok("Tax stamp status updated successfully");
    }

    @PutMapping("/updateMedicalVisitStatus/{id}")
    public ResponseEntity<String> updateMedicalVisitStatus(
            @PathVariable Long id,
            @RequestParam String medicalVisitStatus) {
        applicationFileService.updateMedicalVisitStatus(id, medicalVisitStatus);
        return ResponseEntity.ok("Medical visit status updated successfully");
    }

    @PutMapping("/cancelApplicationFile/{id}")
    public ResponseEntity<String> cancelApplicationFile(@PathVariable Long id) {
        applicationFileService.cancelApplicationFile(id);
        return ResponseEntity.ok("Application file cancelled successfully");
    }

    @PutMapping("/updateTheoreticalHours/{id}")
    public ResponseEntity<String> updateTheoreticalHours(
            @PathVariable Long id,
            @RequestParam Double hours) {
        applicationFileService.updateTheoreticalHours(id, hours);
        return ResponseEntity.ok("Theoretical hours updated successfully");
    }

    @PutMapping("/updatePracticalHours/{id}")
    public ResponseEntity<String> updatePracticalHours(
            @PathVariable Long id,
            @RequestParam Double hours) {
        applicationFileService.updatePracticalHours(id, hours);
        return ResponseEntity.ok("Practical hours updated successfully");
    }

    @PutMapping("/closeApplicationFile/{id}")
    public ApplicationFileCloseResponseDTO closeApplicationFile(@PathVariable Long id) {
        return applicationFileService.closeApplicationFile(id);
    }
}