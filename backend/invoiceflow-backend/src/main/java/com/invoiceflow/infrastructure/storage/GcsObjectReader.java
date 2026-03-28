package com.invoiceflow.infrastructure.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;

@ApplicationScoped
public class GcsObjectReader {

    @ConfigProperty(name = "gcs.bucket.name")
    String bucketName;

    @ConfigProperty(name = "gcs.credentials.path")
    String serviceAccountPath;

    public byte[] readBytes(String objectName) {
        Storage storage = buildStorage();
        Blob blob = storage.get(bucketName, objectName);
        if (blob == null) {
            throw new IllegalStateException("GCS object not found: " + objectName);
        }
        return blob.getContent();
    }

    public String readContentType(String objectName) {
        Storage storage = buildStorage();
        Blob blob = storage.get(bucketName, objectName);
        if (blob == null) {
            throw new IllegalStateException("GCS object not found: " + objectName);
        }
        return blob.getContentType();
    }

    private Storage buildStorage() {
        try (FileInputStream in = new FileInputStream(serviceAccountPath)) {
            GoogleCredentials creds = GoogleCredentials.fromStream(in);
            return StorageOptions.newBuilder().setCredentials(creds).build().getService();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load GCS credentials", e);
        }
    }
}