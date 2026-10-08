package com.example.demo.controller;

import com.example.demo.service.ChatService;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/chat")
    public class ChatController {

        private final ChatService chatService;

        public ChatController(ChatService chatService) {
            this.chatService = chatService;
        }

        @PostMapping("/{sessionId}")
        public String chat(
                @PathVariable Long sessionId,
                @RequestBody String message) {

            
            return chatService.sendMessage(
                    sessionId,
                    message);
        }
}
