package com.example.demo.entity;

import java.util.List;

public class ChatSessionList {

    public List<ChatSession> getChatSessionList() {
        return chatSessionList;
    }

    public void setChatSessionList(List<ChatSession> chatSessionList) {
        this.chatSessionList = chatSessionList;
    }

    private List<ChatSession> chatSessionList;

}
