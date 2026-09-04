package com.example.demo.controller;

import com.example.demo.dto.ChatMessageDTO;
import com.example.demo.dto.ChatSummaryDTO;
import com.example.demo.entity.ChatSession;
import com.example.demo.entity.ChatSessionList;
import com.example.demo.service.ChatSessionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatSessionController {

    private final ChatSessionService service;

    public ChatSessionController(
            ChatSessionService service) {

        this.service = service;
    }

    @PostMapping
    public ChatSession createChat() {

        return service.createSession();
    }

    @GetMapping
    public List<ChatSummaryDTO> getChats() {
        return service.getAllSessions()
                .stream()
                .map(chat ->
                        new ChatSummaryDTO(
                                chat.getId(),
                                chat.getTitle()))
                .toList();
    }

    @GetMapping("/{id}")
    public List<ChatMessageDTO> getChat(
            @PathVariable Long id) {

        return service.getSession(id)
                .getMessages()
                .stream()
                .map(message ->
                        new ChatMessageDTO(
                                message.getRole().name(),
                                message.getContent()))
                .toList();
    }

    @DeleteMapping("/{id}")
    public void deleteChat(
            @PathVariable Long id) {

        service.deleteSession(id);
    }
}
