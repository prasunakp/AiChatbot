package com.example.demo.service;

import com.example.demo.entity.ChatSession;
import com.example.demo.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatSessionService {

    private final ChatSessionRepository repository;

    public ChatSessionService(ChatSessionRepository repository) {
        this.repository = repository;
    }

    public ChatSession createSession() {

        ChatSession session = new ChatSession();

        session.setTitle("New Chat");

        return repository.save(session);
    }
    public List<ChatSession> getAllSessions() {

        return repository.findAllByOrderByUpdatedAtDesc();
    }

    public ChatSession getSession(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Chat session not found"));
    }

    public void deleteSession(Long id) {

        repository.deleteById(id);
    }

}
