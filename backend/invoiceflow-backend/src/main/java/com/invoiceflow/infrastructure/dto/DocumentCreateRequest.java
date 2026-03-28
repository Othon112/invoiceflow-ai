package com.invoiceflow.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentCreateRequest {
    private String originalFilename;
    private String mimeType;
    private Long sizeBytes;
    private String sha256Hash;
    private String storagePath;
}