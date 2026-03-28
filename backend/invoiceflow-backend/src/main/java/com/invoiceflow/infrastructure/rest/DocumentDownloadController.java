package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.application.service.AuthorizationService;
import com.invoiceflow.domain.model.Document;
import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.DocumentRepository;
import com.invoiceflow.domain.repository.UserRepository;
import com.invoiceflow.infrastructure.storage.GcsSignedUrlService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import java.net.URL;
import java.util.UUID;

@Path("/api/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentDownloadController {

    @Inject
    DocumentRepository documentRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    GcsSignedUrlService gcsSignedUrlService;

    @Inject
    AuthorizationService authorizationService;

    @Context
    ContainerRequestContext ctx;

    @GET
    @Path("/{id}/download")
    public DownloadResponse download(@PathParam("id") UUID documentId) {

        String firebaseUid = (String) ctx.getProperty("firebase.uid");
        if (firebaseUid == null) {
            throw new IllegalStateException("Missing firebase.uid");
        }

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new NotFoundException("User not found"));

        authorizationService.requireReadAccess(user);

        Document document = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        // 🔐 Seguridad: mismo company
        if (!document.getCompanyId().equals(user.getCompanyId())) {
            throw new ForbiddenException("You cannot access this document");
        }

        URL signedUrl = gcsSignedUrlService.createDownloadUrl(
                document.getStoragePath(),
                document.getMimeType()
        );

        return new DownloadResponse(signedUrl.toString());
    }

    public static class DownloadResponse {
        public String downloadUrl;

        public DownloadResponse(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }
}