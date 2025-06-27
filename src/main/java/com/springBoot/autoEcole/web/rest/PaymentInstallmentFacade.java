package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.dto.PaymentWithInstallmentsDTO;
import com.springBoot.autoEcole.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.PaymentInstallment;
import com.springBoot.autoEcole.service.PaymentInstallmentService;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/paymentInstallment")
@CrossOrigin
public class PaymentInstallmentFacade {

    @Autowired
    private PaymentInstallmentService paymentInstallmentService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/getPaymentInstallment/{id}")
    public PaymentInstallment getPaymentInstallmentById(@PathVariable Long id) {
        return paymentInstallmentService.findById(id);
    }

    @PostMapping("/savePaymentInstallment/{paymentId}")
    public ResponseEntity<?> savePaymentInstallment(
            @PathVariable Long paymentId,
            @RequestParam Integer amount) {
        try {
            PaymentWithInstallmentsDTO result = paymentInstallmentService.savePaymentInstallment(paymentId, amount);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving payment installment: " + e.getMessage());
        }
    }
}