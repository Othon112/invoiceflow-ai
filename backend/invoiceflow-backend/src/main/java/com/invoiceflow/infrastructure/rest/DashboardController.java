package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.application.service.AuthorizationService;
import com.invoiceflow.application.usecase.GetDashboardSummaryUseCase;
import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.UserRepository;
import com.invoiceflow.infrastructure.dto.DashboardSummaryResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("/api/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardController {
    @Inject
    GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    @Inject
    UserRepository userRepository;

    @Inject
    AuthorizationService authorizationService;

    @Context
    ContainerRequestContext ctx;

    @GET
    @Path("/summary")
    public DashboardSummaryResponse summary() {
        User user = requireUser();
        authorizationService.requireReadAccess(user);
        return getDashboardSummaryUseCase.execute(user.getCompanyId());
    }
    private User requireUser() {
        String firebaseUid = (String) ctx.getProperty("firebase.uid");
        if (firebaseUid == null) {
            throw new IllegalStateException("Missing firebase.uid");
        }

        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
