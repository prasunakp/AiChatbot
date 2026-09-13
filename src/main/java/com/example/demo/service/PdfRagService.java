package com.example.demo.service;
import org.apache.tika.Tika;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PdfRagService {

    private final VectorStore vectorStore;

    private final Tika tika = new Tika();

    public PdfRagService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String processPdf(MultipartFile file)
            throws Exception {

        // 1. Extract text

        String text =
                tika.parseToString(
                        file.getInputStream());


        if (text == null || text.isBlank()) {

            throw new IllegalArgumentException(
                    "No text found in PDF");
        }


        // 2. Create document

        Document document =
                new Document(text);


        // 3. Split into chunks


        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(350)
                .withMinChunkLengthToEmbed(5)
                .build();

        List<Document> chunks =
                splitter.apply(
                        List.of(document));


        // 4. Add metadata

        for (Document chunk : chunks) {

            chunk.getMetadata().put(
                    "source",
                    file.getOriginalFilename());
        }


        // 5. Generate embeddings
        //    and store in pgvector

        vectorStore.add(chunks);


        return "PDF processed successfully. "
                + chunks.size()
                + " chunks stored.";
    }
}