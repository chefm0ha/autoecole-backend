package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.dto.AddApplicationFileRequestDTO;
import com.springBoot.autoEcole.dto.ApplicationFileDTO;
import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.service.ApplicationFileService;
import com.springBoot.autoEcole.service.impl.ApplicationFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applicationFile")
@CrossOrigin
public class ApplicationFileFacade {

    @Autowired
    private ApplicationFileService applicationFileService;

    @PostMapping("/saveApplicationFile/{candidateCin}")
    public ResponseEntity<?> saveApplicationFile(@PathVariable String candidateCin, @RequestBody AddApplicationFileRequestDTO request) {
        try {
            ApplicationFileDTO result = applicationFileService.saveApplicationFile(candidateCin, request);
            return ResponseEntity.ok(result);
        } catch (ApplicationFileServiceImpl.ApplicationFileException e) {
            // Business rule violations with specific error codes
            Map<String, Object> error = new HashMap<>();
            error.put("code", e.getErrorCode());
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (EntityNotFoundException e) {
            // Entity not found
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            // Any other unexpected errors
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "Error saving application file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/updateApplicationFile/{id}")
    public ResponseEntity<?> updateApplicationFile(@PathVariable Long id, @RequestBody ApplicationFile applicationFile) {
        try {
            ApplicationFile result = applicationFileService.updateApplicationFile(id, applicationFile);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "ENTITY_NOT_FOUND");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_SERVER_ERROR");
            error.put("message", "Error updating application file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getApplicationFile/{id}")
    public ResponseEntity<?> getApplicationFileById(@PathVariable Long id) {
        try {
            ApplicationFile result = applicationFileService.findById(id);
            if (result == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "ENTITY_NOT_FOUND");
                error.put("message", "Application file not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_SERVER_ERROR");
            error.put("message", "Error retrieving application file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/deleteApplicationFile/{id}")
    public ResponseEntity<?> deleteApplicationFile(@PathVariable Long id) {
        try {
            Long result = applicationFileService.deleteApplicationFile(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_SERVER_ERROR");
            error.put("message", "Error deleting application file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getApplicationFileByCandidate/{candidateCin}")
    public ResponseEntity<?> getApplicationFileByCandidate(@PathVariable String candidateCin) {
        try {
            List<ApplicationFileDTO> result = applicationFileService.getApplicationFilesByCandidate(candidateCin);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "ENTITY_NOT_FOUND");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_SERVER_ERROR");
            error.put("message", "Error retrieving application files: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/updateTaxStampStatus/{id}")
    public ResponseEntity<?> updateTaxStampStatus(@PathVariable Long id, @RequestParam String taxStampStatus) {
        try {
            applicationFileService.updateTaxStampStatus(id, taxStampStatus);

            Map<String, Object> success = new HashMap<>();
            success.put("message", "Tax stamp status updated successfully");
            return ResponseEntity.ok(success);
        } catch (EntityNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "ENTITY_NOT_FOUND");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_SERVER_ERROR");
            error.put("message", "Error updating tax stamp status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/updateMedicalVisitStatus/{id}")
    public ResponseEntity<?> updateMedicalVisitStatus(@PathVariable Long id, @RequestParam String medicalVisitStatus) {
        try {
            applicationFileService.updateMedicalVisitStatus(id, medicalVisitStatus);

            Map<String, Object> success = new HashMap<>();
            success.put("message", "Medical visit status updated successfully");
            return ResponseEntity.ok(success);
        } catch (EntityNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "ENTITY_NOT_FOUND");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_SERVER_ERROR");
            error.put("message", "Error updating medical visit status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/cancelApplicationFile/{id}")
    public ResponseEntity<?> cancelApplicationFile(@PathVariable Long id) {
        try {
            applicationFileService.cancelApplicationFile(id);
            Map<String, Object> success = new HashMap<>();
            success.put("message", "Application file cancelled successfully");
            return ResponseEntity.ok(success);
        } catch (ApplicationFileServiceImpl.ApplicationFileException e) {
            // Business rule violations with specific error codes
            Map<String, Object> error = new HashMap<>();
            error.put("code", e.getErrorCode());
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (EntityNotFoundException e) {
            // Application file not found
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            // Any other unexpected errors
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "Error cancelling application file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/updateTheoreticalHours/{id}")
    public ResponseEntity<?> updateTheoreticalHours(@PathVariable Long id, @RequestParam Double hours) {
        try {
            applicationFileService.updateTheoreticalHours(id, hours);

            Map<String, Object> success = new HashMap<>();
            success.put("message", "Theoretical hours updated successfully");
            return ResponseEntity.ok(success);
        } catch (EntityNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "ENTITY_NOT_FOUND");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INVALID_INPUT");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_SERVER_ERROR");
            error.put("message", "Error updating theoretical hours: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/updatePracticalHours/{id}")
    public ResponseEntity<?> updatePracticalHours(@PathVariable Long id, @RequestParam Double hours) {
        try {
            applicationFileService.updatePracticalHours(id, hours);

            Map<String, Object> success = new HashMap<>();
            success.put("message", "Practical hours updated successfully");
            return ResponseEntity.ok(success);
        } catch (EntityNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "ENTITY_NOT_FOUND");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INVALID_INPUT");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_SERVER_ERROR");
            error.put("message", "Error updating practical hours: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/closeApplicationFile/{id}")
    public ResponseEntity<?> closeApplicationFile(@PathVariable Long id) {
        try {
            // Check eligibility first for informative response
            boolean isEligible = applicationFileService.isEligibleForCompletion(id);

            applicationFileService.closeApplicationFile(id);

            Map<String, Object> success = new HashMap<>();
            if (isEligible) {
                success.put("message", "Application file completed successfully");
                success.put("status", "COMPLETED");
            } else {
                success.put("message", "Application file cancelled - not eligible for completion");
                success.put("status", "CANCELLED");
                success.put("reason", "Both theory and practical exams must be passed to complete the application file");
            }

            return ResponseEntity.ok(success);
        } catch (ApplicationFileServiceImpl.ApplicationFileException e) {
            // Business rule violations with specific error codes
            Map<String, Object> error = new HashMap<>();
            error.put("code", e.getErrorCode());
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (EntityNotFoundException e) {
            // Application file not found
            Map<String, Object> error = new HashMap<>();
            error.put("code", 404);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            // Any other unexpected errors
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("message", "Error closing application file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}