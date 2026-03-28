package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.application.service.AuthorizationService;
import com.invoiceflow.application.usecase.CreateDocumentUseCase;
import com.invoiceflow.domain.model.Document;
import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.DocumentRepository;
import com.invoiceflow.domain.repository.UserRepository;
import com.invoiceflow.infrastructure.dto.DocumentUploadRequest;
import com.invoiceflow.infrastructure.dto.PageResponse;
import com.invoiceflow.infrastructure.storage.GcsStorageService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

@Path("/api/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentController {

    @Inject
    CreateDocumentUseCase createDocumentUseCase;

    @Inject
    UserRepository userRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    GcsStorageService gcsStorageService;

    @Inject
    AuthorizationService authorizationService;

    @Context
    ContainerRequestContext ctx;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Document upload(DocumentUploadRequest request) throws Exception {
        User user = requireUser();
        authorizationService.requireAccountantOrAdmin(user);

        FileUpload file = request.file;
        if (file == null) {
            throw new IllegalStateException("Missing file");
        }

        byte[] bytes = Files.readAllBytes(file.uploadedFile());
        String sha256 = sha256Hex(bytes);

        Optional<Document> existing =
                documentRepository.findByCompanyAndHash(user.getCompanyId(), sha256);
        if (existing.isPresent()) {
            return existing.get();
        }

        String originalFilename = file.fileName();
        String mimeType = file.contentType();

        String objectName =
                "companies/" + user.getCompanyId() + "/documents/" + sha256 + "_" + originalFilename;

        gcsStorageService.upload(objectName, bytes, mimeType);

        Document doc = new Document();
        doc.setCompanyId(user.getCompanyId());
        doc.setUploadedByUserId(user.getId());
        doc.setOriginalFilename(originalFilename);
        doc.setMimeType(mimeType);
        doc.setSizeBytes((long) bytes.length);
        doc.setSha256Hash(sha256);
        doc.setStoragePath(objectName);

        return createDocumentUseCase.execute(doc);
    }

    @GET
    public PageResponse<Document> list(
            @QueryParam("limit") @DefaultValue("20") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset
    ) {
        if (limit < 1 || limit > 100) {
            throw new BadRequestException("limit must be between 1 and 100");
        }
        if (offset < 0) {
            throw new BadRequestException("offset must be >= 0");
        }

        User user = requireUser();
        authorizationService.requireReadAccess(user);

        long total = documentRepository.countByCompany(user.getCompanyId());
        var items = documentRepository.listByCompany(user.getCompanyId(), limit, offset);

        return new PageResponse<>(items, limit, offset, total);
    }

    private User requireUser() {
        String firebaseUid = (String) ctx.getProperty("firebase.uid");
        if (firebaseUid == null) {
            throw new IllegalStateException("Missing firebase.uid");
        }

        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private static String sha256Hex(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        return HexFormat.of().formatHex(hash);
    }
}