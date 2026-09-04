package com.example.demo.service;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.ChatSession;
import com.example.demo.entity.MessageRole;
import com.example.demo.repository.ChatSessionRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    private final ChatSessionRepository sessionRepository;

    public ChatService(
            ChatClient.Builder builder,
            ChatSessionRepository sessionRepository) {

        this.chatClient = builder.build();

        this.sessionRepository = sessionRepository;
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
        String aiResponse =
                chatClient
                        .prompt()
                        .user(conversation.toString())
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
