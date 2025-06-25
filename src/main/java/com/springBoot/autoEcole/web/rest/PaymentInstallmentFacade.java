package com.springBoot.autoEcole.web.rest;

import com.springBoot.autoEcole.dto.PaymentWithInstallmentsDTO;
import com.springBoot.autoEcole.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springBoot.autoEcole.model.PaymentInstallment;
import com.springBoot.autoEcole.service.PaymentInstallmentService;

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
    public PaymentInstallment savePaymentInstallment(@PathVariable Long paymentId, @RequestBody PaymentInstallment paymentInstallment) {
        return paymentInstallmentService.savePaymentInstallment(paymentId, paymentInstallment);
    }
}