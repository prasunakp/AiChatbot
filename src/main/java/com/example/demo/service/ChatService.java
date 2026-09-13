package com.example.demo.service;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.ChatSession;
import com.example.demo.entity.MessageRole;
import com.example.demo.repository.ChatSessionRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;

    private final VectorStore vectorStore;

    private final ChatSessionRepository sessionRepository;

    public ChatService(
            ChatClient.Builder builder,
            ChatSessionRepository sessionRepository,
            VectorStore vectorStore) {

        this.chatClient = builder.build();

        this.sessionRepository = sessionRepository;
        this.vectorStore=vectorStore;
    }
    public String sendMessage(
            Long sessionId,
            String userMessage) {

        ChatSession session =
                sessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Session not found"));

        // Save user message

        ChatMessage userMessageEntity =
                new ChatMessage();

        userMessageEntity.setRole(
                MessageRole.USER);

        userMessageEntity.setContent(
                userMessage);

        session.addMessage(
                userMessageEntity);
        StringBuilder conversation =
                new StringBuilder();

        for (ChatMessage message :
                session.getMessages()) {

            conversation
                    .append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");
        }

        List<Document> relevantDocuments =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(userMessage)
                                .topK(5)
                                .build()
                );

        String context =
                relevantDocuments.stream()
                        .map(Document::getText)
                        .collect(
                                Collectors.joining("\n\n")
                        );

        String prompt = """
                        You are a helpful AI assistant.
                               
                               You have access to information retrieved from an uploaded PDF.
                               
                               If the user's question can be answered using the PDF context,
                               use the PDF context.
                               
                               If the question is unrelated to the PDF, answer it normally
                               using your general knowledge.
                               
                               If the question is related to the PDF but the answer is not
                               available in the context, clearly say that the information
                               was not found in the uploaded PDF.
                DOCUMENT CONTEXT:
                %s

                USER QUESTION:
                %s
                """.formatted(
                context,
                userMessage
        );


       /* String aiResponse =
                chatClient
                        .prompt()
                        .user(conversation.toString())
                        .call()
                        .content();*/
        String aiResponse =
                chatClient
                        .prompt()
                        .user(prompt)
                        .call()
                        .content();

        // Save AI response

        ChatMessage assistantMessage =
                new ChatMessage();

        assistantMessage.setRole(
                MessageRole.ASSISTANT);

        assistantMessage.setContent(
                aiResponse);

        session.addMessage(
                assistantMessage);

        /*
         * Give conversation a title
         */

        if ("New Chat".equals(session.getTitle())) {

            String title =
                    userMessage.length() > 40
                            ? userMessage.substring(0, 40) + "..."
                            : userMessage;

            session.setTitle(title);
        }

        sessionRepository.save(session);

        return aiResponse;
    }

}
