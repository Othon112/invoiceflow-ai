package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.application.service.BootstrapService;
import com.invoiceflow.application.usecase.BootstrapUseCase;
import com.invoiceflow.infrastructure.dto.admin.BootstrapRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminBootstrapController {

    @Inject
    BootstrapUseCase bootstrapUseCase;

    @Inject
    FirebaseRequestContext firebase;

    @POST
    @Path("/bootstrap")
    public BootstrapService.Result bootstrap(BootstrapRequest request) {
        String uid = firebase.uid();
        String email = firebase.email();

        if (uid == null || uid.isBlank()) {
            throw new IllegalStateException("Missing firebase uid");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Missing firebase email");
        }
        if (request == null || request.companyName == null || request.companyName.isBlank()) {
            throw new IllegalArgumentException("companyName is required");
        }

        return bootstrapUseCase.execute(request.companyName.trim(), uid, email.trim());
    }
}