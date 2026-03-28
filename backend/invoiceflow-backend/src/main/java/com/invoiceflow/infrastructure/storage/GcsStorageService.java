package com.invoiceflow.infrastructure.storage;

public interface GcsStorageService {
    void upload(String objectName, byte[] bytes, String contentType);
    byte[] download(String objectName);
    void delete(String objectName);
}