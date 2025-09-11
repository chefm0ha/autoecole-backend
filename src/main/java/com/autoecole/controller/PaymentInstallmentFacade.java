package com.autoecole.controller;

import com.autoecole.dto.PaymentWithInstallmentsDTO;
import lombok.AllArgsConstructor;
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
}