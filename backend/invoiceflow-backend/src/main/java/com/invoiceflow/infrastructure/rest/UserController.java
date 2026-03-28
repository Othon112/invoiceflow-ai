package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.application.usecase.GetCurrentUserUseCase;
import com.invoiceflow.domain.model.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;

@Path("/api")
public class UserController {

    @Inject
    GetCurrentUserUseCase getCurrentUserUseCase;

    @Context
    ContainerRequestContext requestContext;

    @GET
    @Path("/me")
    public User me() {

        String uid = (String) requestContext.getProperty("firebase.uid");
        String email = (String) requestContext.getProperty("firebase.email");

        if (uid == null || email == null) {
            throw new IllegalStateException("Missing Firebase authentication data");
        }

        return getCurrentUserUseCase.execute(uid, email);
    }
}