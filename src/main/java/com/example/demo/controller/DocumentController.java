package com.example.demo.controller;

import com.example.demo.service.DocumentService;
import com.example.demo.service.PdfRagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final PdfRagService pdfRagService;

    public DocumentController(PdfRagService pdfRagService) {
        this.pdfRagService = pdfRagService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(
            @RequestParam("file")
            MultipartFile file) {
        try {
            String result =
                    pdfRagService.processPdf(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body("PDF processing failed");
        }
    }
}
