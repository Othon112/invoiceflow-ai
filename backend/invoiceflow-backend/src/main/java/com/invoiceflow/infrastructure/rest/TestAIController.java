package com.invoiceflow.infrastructure.rest;

import com.invoiceflow.infrastructure.ai.GeminiExtractionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/test-ai")
@Produces(MediaType.TEXT_PLAIN)
public class TestAIController {

    @Inject
    GeminiExtractionService geminiService;

    @GET
    public String test() {

        String fakeText = """
                Amazon
                Invoice: INV-2026-001
                Date: 2026-03-20
                Subtotal: 100.00 EUR
                Tax: 19.00 EUR
                Total: 119.00 EUR
                VAT ID: DE123456789
                """;

        return geminiService.extractFromText(fakeText);
    }
}
