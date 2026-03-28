package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.application.service.AuthorizationService;
import com.invoiceflow.application.usecase.*;
import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.UserRepository;
import com.invoiceflow.infrastructure.dto.InvoiceCreateFromDocumentRequest;
import com.invoiceflow.infrastructure.dto.InvoiceDetailRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/api/invoices")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InvoiceController {

    @Inject
    CreateInvoiceFromDocumentUseCase createFromDocumentUseCase;

    @Inject
    ListInvoicesUseCase listInvoicesUseCase;

    @Inject
    GetInvoiceByIdUseCase getInvoiceByIdUseCase;

    @Inject
    ExtractInvoiceUseCase extractInvoiceUseCase;
    @Inject
    ApproveInvoiceUseCase approveInvoiceUseCase;
    @Inject
    RejectInvoiceUseCase rejectInvoiceUseCase;

    @Inject
    UserRepository userRepository;

    @Inject
    AuthorizationService authorizationService;

    @Inject
    GetInvoiceDetailUseCase getInvoiceDetailUseCase;

    @Inject
    AnalyzeInvoiceUseCase analyzeInvoiceUseCase;

    @Context
    ContainerRequestContext ctx;

    @POST
    @Path("/{id}/analyze")
    public Invoice analyze(@PathParam("id") UUID invoiceId) {

        User user = requireUser();

        authorizationService.requireAccountantOrAdmin(user);

        return analyzeInvoiceUseCase.execute(
                invoiceId,
                user.getCompanyId()
        );
    }

    @POST
    @Path("/from-document")
    public Invoice createFromDocument(InvoiceCreateFromDocumentRequest request) {
        User user = requireUser();

        authorizationService.requireAccountantOrAdmin(user);

        return createFromDocumentUseCase.execute(
                request.documentId,
                user.getId(),
                user.getCompanyId()
        );
    }

    @GET
    public ListInvoicesUseCase.Result list(
            @QueryParam("limit") @DefaultValue("20") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset
    ) {
        User user = requireUser();
        authorizationService.requireReadAccess(user);

        return listInvoicesUseCase.execute(user.getCompanyId(), limit, offset);
    }
    @GET
    @Path("/{id}")
    public InvoiceDetailRequest getById(@PathParam("id") UUID id) {
        User user = requireUser();
        authorizationService.requireReadAccess(user);
        return getInvoiceDetailUseCase.execute(id, user.getCompanyId());
    }

    @POST
    @Path("/{id}/extract")
    public Invoice extract(@PathParam("id") UUID invoiceId) {
        User user = requireUser();
        authorizationService.requireAccountantOrAdmin(user);
        return extractInvoiceUseCase.execute(invoiceId, user.getCompanyId());
    }
    @POST
    @Path("/{id}/approve")
    public Invoice approve(@PathParam("id") UUID invoiceId) {
        User user = requireUser();
        authorizationService.requireReviewerOrAdmin(user);
        return approveInvoiceUseCase.execute(invoiceId, user.getCompanyId());
    }

    @POST
    @Path("/{id}/reject")
    public Invoice reject(@PathParam("id") UUID invoiceId) {
        User user = requireUser();
        authorizationService.requireReviewerOrAdmin(user);
        return rejectInvoiceUseCase.execute(invoiceId, user.getCompanyId());
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