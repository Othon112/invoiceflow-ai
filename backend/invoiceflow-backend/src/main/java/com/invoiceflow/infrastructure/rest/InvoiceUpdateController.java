package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.application.usecase.UpdateInvoiceUseCase;
import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.UserRepository;
import com.invoiceflow.infrastructure.dto.InvoiceUpdateRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/api/invoices")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InvoiceUpdateController {

    @Inject
    UpdateInvoiceUseCase updateInvoiceUseCase;

    @Inject
    UserRepository userRepository;

    @Context
    ContainerRequestContext ctx;

    @PATCH
    @Path("/{id}")
    public Invoice update(@PathParam("id") UUID invoiceId, InvoiceUpdateRequest request) {

        String firebaseUid = (String) ctx.getProperty("firebase.uid");
        if (firebaseUid == null) {
            throw new IllegalStateException("Missing firebase.uid");
        }

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Invoice patch = new Invoice();
        patch.setStatus(request.status);

        patch.setVendorName(request.vendorName);
        patch.setInvoiceNumber(request.invoiceNumber);
        patch.setInvoiceDate(request.invoiceDate);
        patch.setCurrency(request.currency);

        patch.setSubtotalAmount(request.subtotalAmount);
        patch.setTaxAmount(request.taxAmount);
        patch.setTotalAmount(request.totalAmount);

        patch.setVatId(request.vatId);

        return updateInvoiceUseCase.execute(invoiceId, user.getCompanyId(), patch);
    }
}