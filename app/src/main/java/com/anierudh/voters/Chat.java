package com.anierudh.voters;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Chat {
    private String chat;
    private String name;
    private Timestamp timestamp;

    public Chat() {
        // Default constructor required for Firestore
    }

    public String getChat() {
        return chat;
    }

    public String getName() {
        return name;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
