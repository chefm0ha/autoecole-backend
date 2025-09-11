package com.autoecole.controller;

import com.autoecole.dto.response.PaymentWithInstallmentsDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.autoecole.model.PaymentInstallment;
import com.autoecole.service.PaymentInstallmentService;

@RestController
@RequestMapping("/paymentInstallment")
@CrossOrigin
@AllArgsConstructor
public class PaymentInstallmentFacade {

    private final PaymentInstallmentService paymentInstallmentService;

    @GetMapping("/getPaymentInstallment/{id}")
    public PaymentInstallment getPaymentInstallmentById(@PathVariable Long id) {
        return paymentInstallmentService.findById(id);
    }

    @PostMapping("/savePaymentInstallment/{paymentId}")
    public PaymentWithInstallmentsDTO savePaymentInstallment(
            @PathVariable Long paymentId,
            @RequestParam Integer amount) {
        return paymentInstallmentService.savePaymentInstallment(paymentId, amount);
    }

    /**
     * Admin-only endpoint to validate a pending payment installment
     */
    @PutMapping("/validatePaymentInstallment/{installmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentWithInstallmentsDTO> validatePaymentInstallment(
            @PathVariable Long installmentId) {
        PaymentWithInstallmentsDTO result = paymentInstallmentService.validatePaymentInstallment(installmentId);
        return ResponseEntity.ok(result);
    }
}