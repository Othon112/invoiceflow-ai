package com.invoiceflow.infrastructure.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;
import java.io.IOException;

@ApplicationScoped
public class GcsClientProvider {

    @ConfigProperty(name = "gcs.credentials.path")
    String credentialsPath;

    public Storage storage() {
        try (FileInputStream in = new FileInputStream(credentialsPath)) {
            GoogleCredentials creds = GoogleCredentials.fromStream(in);
            return StorageOptions.newBuilder()
                    .setCredentials(creds)
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load GCS credentials from: " + credentialsPath, e);
        }
    }
}