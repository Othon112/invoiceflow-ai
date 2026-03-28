package com.invoiceflow.application.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class InvoiceInsightService {

    @ConfigProperty(name = "vertex.project-id")
    String projectId;

    @ConfigProperty(name = "vertex.location")
    String location;

    @ConfigProperty(name = "vertex.model")
    String model;

    public String generateInsight(String vendorName, Long totalAmount, String riskLevel, String flagsJson) {

        String prompt = """
                You are a financial risk assistant.

                Analyze this invoice risk information:

                Vendor: %s
                Amount: %s
                Risk Level: %s
                Flags: %s

                Explain in 1 or 2 short sentences why this invoice might be risky.
                
                IMPORTANT:
                - Return ONLY plain text
                - No JSON
                - No markdown
                - No bullet points
                - Be concise and professional
                """.formatted(vendorName, totalAmount, riskLevel, flagsJson);

        try (Client client = Client.builder()
                .vertexAI(true)
                .project(projectId)
                .location(location)
                .build()) {

            GenerateContentResponse response =
                    client.models.generateContent(model, prompt, null);

            System.out.println("=== GEMINI INSIGHT RAW RESPONSE ===");
            System.out.println(response);

            String text = response.text();

            System.out.println("=== GEMINI INSIGHT TEXT ===");
            System.out.println(text);

            if (text == null || text.isBlank()) {
                return "No insight generated";
            }

            return text.trim();

        } catch (Exception e) {
            System.out.println("=== GEMINI INSIGHT ERROR ===");
            e.printStackTrace();
            return "Unable to generate insight: " + e.getMessage();
        }
    }
}