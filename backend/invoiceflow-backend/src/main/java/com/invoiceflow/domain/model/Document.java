package com.invoiceflow.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    private UUID id;
    private UUID companyId;
    private UUID uploadedByUserId;

    private String storagePath;
    private String originalFilename;
    private String mimeType;
    private long sizeBytes;
    private String sha256Hash;

    private OffsetDateTime createdAt;
}
