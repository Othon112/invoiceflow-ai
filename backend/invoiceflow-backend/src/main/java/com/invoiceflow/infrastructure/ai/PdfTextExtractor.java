package com.invoiceflow.infrastructure.ai;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayInputStream;

@ApplicationScoped
public class PdfTextExtractor {

    public String extractText(byte[] fileBytes) {
        try (PDDocument document = Loader.loadPDF(new ByteArrayInputStream(fileBytes).readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from PDF", e);
        }
    }
}