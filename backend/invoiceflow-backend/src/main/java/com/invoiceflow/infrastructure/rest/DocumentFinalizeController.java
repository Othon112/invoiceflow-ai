package com.invoiceflow.infrastructure.rest;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.invoiceflow.application.usecase.FinalizeDocumentUseCase;
import com.invoiceflow.domain.model.Document;
import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.UserRepository;
import com.invoiceflow.infrastructure.dto.DocumentFinalizeRequest;
import com.invoiceflow.infrastructure.storage.GcsClientProvider;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentFinalizeController {

    @Inject
    FinalizeDocumentUseCase finalizeDocumentUseCase;

    @Inject
    UserRepository userRepository;

    @Inject
    GcsClientProvider gcsClientProvider;

    @ConfigProperty(name = "gcs.bucket.name")
    String bucketName;

    @Context
    ContainerRequestContext ctx;

    @POST
    @Path("/finalize")
    @Consumes(MediaType.APPLICATION_JSON)
    public Document finalizeUpload(DocumentFinalizeRequest request) {
        if (request == null || request.objectName == null || request.sha256Hash == null) {
            throw new BadRequestException("objectName and sha256Hash are required");
        }

        String firebaseUid = (String) ctx.getProperty("firebase.uid");
        if (firebaseUid == null) {
            throw new IllegalStateException("Missing firebase.uid");
        }

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!request.objectName.startsWith("companies/" + user.getCompanyId() + "/")) {
            throw new BadRequestException("objectName does not belong to your company");
        }

        Storage storage = gcsClientProvider.storage();
        Blob blob = storage.get(bucketName, request.objectName);

        if (blob == null) {
            throw new BadRequestException("Object not found in GCS: " + request.objectName);
        }

        Document doc = new Document();
        doc.setCompanyId(user.getCompanyId());
        doc.setUploadedByUserId(user.getId());
        doc.setStoragePath(request.objectName);
        doc.setSha256Hash(request.sha256Hash);
        doc.setOriginalFilename(request.originalFilename != null ? request.originalFilename : blob.getName());
        doc.setMimeType(blob.getContentType());
        doc.setSizeBytes(blob.getSize());

        return finalizeDocumentUseCase.execute(doc);
    }
}