package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.application.usecase.CreateInviteUseCase;
import com.invoiceflow.domain.model.Invite;
import com.invoiceflow.infrastructure.dto.admin.InviteRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("/api/admin/invites")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminInviteController {

    @Inject
    CreateInviteUseCase createInviteUseCase;

    @POST
    public Invite createInvite(
            InviteRequest request,
            @HeaderParam("Authorization") String authHeader,
            @Context jakarta.ws.rs.container.ContainerRequestContext context
    ) {
        String firebaseUid = (String) context.getProperty("firebase.uid");

        return createInviteUseCase.execute(
                firebaseUid,
                request.email,
                request.role
        );
    }
}