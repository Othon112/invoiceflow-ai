package com.invoiceflow.infrastructure.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class GcsSignedUrlService {

    @ConfigProperty(name = "gcs.bucket.name")
    String bucketName;

    @ConfigProperty(name = "gcs.credentials.path")
    String serviceAccountPath;

    public URL createUploadUrl(String objectName, String contentType) {
        Storage storage = buildStorage();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(contentType)
                .build();

        return storage.signUrl(
                blobInfo,
                10,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(com.google.cloud.storage.HttpMethod.PUT),
                Storage.SignUrlOption.withV4Signature(),
                Storage.SignUrlOption.withContentType()
        );
    }

    public String generateObjectName(UUID companyId, String originalFilename) {
        String safeName = originalFilename == null ? "file" : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "companies/" + companyId + "/uploads/" + UUID.randomUUID() + "_" + safeName;
    }

    private Storage buildStorage() {
        try (FileInputStream in = new FileInputStream(serviceAccountPath)) {
            GoogleCredentials creds = GoogleCredentials.fromStream(in);
            return StorageOptions.newBuilder()
                    .setCredentials(creds)
                    .build()
                    .getService();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load GCS credentials for signed URL", e);
        }
    }

    public URL createDownloadUrl(String objectName, String contentType) {
        Storage storage = buildStorage();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(contentType)
                .build();

        return storage.signUrl(
                blobInfo,
                5,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(com.google.cloud.storage.HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature()
        );
    }
}