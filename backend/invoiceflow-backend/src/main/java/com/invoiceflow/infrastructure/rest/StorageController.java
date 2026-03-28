package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.UserRepository;
import com.invoiceflow.infrastructure.storage.GcsSignedUrlService;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import java.net.URL;

@Path("/api/storage")
@Produces(MediaType.APPLICATION_JSON)
public class StorageController {

    @Inject
    GcsSignedUrlService gcsSignedUrlService;

    @Inject
    UserRepository userRepository;

    @Context
    ContainerRequestContext ctx;

    public static class PresignRequest {
        public String filename;
        public String contentType;
    }

    public static class PresignResponse {
        public String objectName;
        public String uploadUrl;

        public PresignResponse(String objectName, String uploadUrl) {
            this.objectName = objectName;
            this.uploadUrl = uploadUrl;
        }
    }

    @POST
    @Path("/presign")
    @Consumes(MediaType.APPLICATION_JSON)
    public PresignResponse presign(PresignRequest request) {
        if (request == null || request.filename == null || request.contentType == null) {
            throw new BadRequestException("filename and contentType are required");
        }

        String firebaseUid = (String) ctx.getProperty("firebase.uid");
        if (firebaseUid == null) {
            throw new IllegalStateException("Missing firebase.uid");
        }

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String objectName = gcsSignedUrlService.generateObjectName(user.getCompanyId(), request.filename);
        URL url = gcsSignedUrlService.createUploadUrl(objectName, request.contentType);

        return new PresignResponse(objectName, url.toString());
    }
}