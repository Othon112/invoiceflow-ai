package com.invoiceflow.infrastructure.storage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GcsStorageServiceImpl implements GcsStorageService {

    @ConfigProperty(name = "gcs.bucket.name")
    String bucketName;

    private Storage storage() {
        return StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public void upload(String objectName, byte[] bytes, String contentType) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(contentType)
                .build();

        storage().create(blobInfo, bytes);
    }

    @Override
    public byte[] download(String objectName) {
        Blob blob = storage().get(BlobId.of(bucketName, objectName));
        if (blob == null) {
            throw new IllegalStateException("GCS object not found: " + objectName);
        }
        return blob.getContent();
    }

    @Override
    public void delete(String objectName) {
        storage().delete(BlobId.of(bucketName, objectName));
    }
}