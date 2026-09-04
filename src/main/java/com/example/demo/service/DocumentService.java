package com.example.demo.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {
    private final Tika tika = new Tika();
    public String processPdf(
            MultipartFile file) throws Exception {
        if (file.isEmpty()) {

            throw new IllegalArgumentException(
                    "File is empty");
        }


        // Detect file type

        String mediaType =
                tika.detect(file.getInputStream());


        if (!"application/pdf".equals(mediaType)) {

            throw new IllegalArgumentException(
                    "Only PDF files are allowed");
        }
        String text =
                tika.parseToString(
                        file.getInputStream());
        System.out.println(
                "Extracted PDF text:");
        System.out.println(text);
        return "PDF uploaded successfully. "
                + "Extracted "
                + text.length()
                + " characters.";
    }

    }
