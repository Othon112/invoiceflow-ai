package com.invoiceflow.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.invoiceflow.infrastructure.ai.PdfTextExtractor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@ApplicationScoped
public class GeminiInvoiceExtractionService implements InvoiceExtractionService {

    @ConfigProperty(name = "vertex.project-id")
    String projectId;

    @ConfigProperty(name = "vertex.location")
    String location;

    @ConfigProperty(name = "vertex.model")
    String model;

    @Inject
    PdfTextExtractor pdfTextExtractor;

    @Override
    public ExtractedInvoiceData extract(byte[] fileBytes, String mimeType) {
        try (Client client = Client.builder()
                .vertexAI(true)
                .project(projectId)
                .location(location)
                .build()) {

            String text;

            if ("application/pdf".equalsIgnoreCase(mimeType)) {
                text = pdfTextExtractor.extractText(fileBytes);
            } else {
                text = new String(fileBytes, StandardCharsets.UTF_8);
            }

            System.out.println("=== TEXT SENT TO GEMINI ===");
            System.out.println(text);

            String prompt = """
                    Extract the following invoice fields from the text below.

                    Return ONLY valid JSON with this exact structure:
                    {
                      "vendorName": "string or null",
                      "invoiceNumber": "string or null",
                      "invoiceDate": "YYYY-MM-DD or null",
                      "currency": "3-letter code or null",
                      "subtotalAmount": integer cents or null,
                      "taxAmount": integer cents or null,
                      "totalAmount": integer cents or null,
                      "vatId": "string or null"
                    }

                    Rules:
                    - Return integer cents, not decimals. Example: 119.00 -> 11900
                    - If a field is missing, use null
                    - Return JSON only, no markdown, no explanation

                    Invoice text:
                    """ + text;

            GenerateContentResponse response =
                    client.models.generateContent(model, prompt, null);

            String json = response.text();

            System.out.println("=== RAW GEMINI RESPONSE ===");
            System.out.println(json);

            return parseJson(json);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Gemini extraction failed", e);
        }
    }

    private ExtractedInvoiceData parseJson(String json) {
        try {
            String cleaned = cleanJson(json);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(cleaned);

            ExtractedInvoiceData data = new ExtractedInvoiceData();
            data.vendorName = getText(node, "vendorName");
            data.invoiceNumber = getText(node, "invoiceNumber");

            String invoiceDate = getText(node, "invoiceDate");
            data.invoiceDate = invoiceDate == null ? null : LocalDate.parse(invoiceDate);

            data.currency = getText(node, "currency");
            data.subtotalAmount = getLong(node, "subtotalAmount");
            data.taxAmount = getLong(node, "taxAmount");
            data.totalAmount = getLong(node, "totalAmount");
            data.vatId = getText(node, "vatId");

            return data;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini JSON response: " + json, e);
        }
    }

    private String cleanJson(String raw) {
        if (raw == null) {
            return null;
        }

        String cleaned = raw.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }

        return cleaned;
    }

    private String getText(JsonNode node, String field) {
        if (!node.has(field) || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asText();
    }

    private Long getLong(JsonNode node, String field) {
        if (!node.has(field) || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asLong();
    }
}