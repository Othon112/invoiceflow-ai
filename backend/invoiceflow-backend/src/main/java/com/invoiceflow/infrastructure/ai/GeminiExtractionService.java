package com.invoiceflow.infrastructure.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GeminiExtractionService {

    @ConfigProperty(name = "vertex.project-id")
    String projectId;

    @ConfigProperty(name = "vertex.location")
    String location;

    @ConfigProperty(name = "vertex.model")
    String model;

    public String extractFromText(String text) {
        try (Client client = Client.builder()
                .vertexAI(true)
                .project(projectId)
                .location(location)
                .build()) {

            String prompt = """
                    Extract the following fields from this invoice text:

                    - vendorName
                    - invoiceNumber
                    - invoiceDate
                    - currency
                    - subtotalAmount
                    - taxAmount
                    - totalAmount
                    - vatId

                    Return ONLY valid JSON with this structure:

                    {
                      "vendorName": "...",
                      "invoiceNumber": "...",
                      "invoiceDate": "...",
                      "currency": "...",
                      "subtotalAmount": 0,
                      "taxAmount": 0,
                      "totalAmount": 0,
                      "vatId": "..."
                    }

                    Important:
                    - invoiceDate must be YYYY-MM-DD
                    - amounts must be integer cents, not decimals
                    - if a field is missing, return null

                    Invoice text:
                    """ + text;

            GenerateContentResponse response =
                    client.models.generateContent(model, prompt, null);

            return response.text();

        } catch (Exception e) {
            throw new RuntimeException("Gemini extraction failed", e);
        }
    }
}