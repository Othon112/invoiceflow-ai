package com.invoiceflow.infrastructure.dto;

import org.jboss.resteasy.reactive.multipart.FileUpload;
import jakarta.ws.rs.FormParam;

public class DocumentUploadRequest {

    @FormParam("file")
    public FileUpload file;
}