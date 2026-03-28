package com.invoiceflow.application.usecase;

import com.invoiceflow.application.service.BootstrapService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BootstrapUseCase {
    @Inject
    BootstrapService bootstrapService;

    public BootstrapService.Result execute (String companyName, String firebaseUid, String email) {
        return bootstrapService.bootstrap(companyName, firebaseUid, email);
    }
}
