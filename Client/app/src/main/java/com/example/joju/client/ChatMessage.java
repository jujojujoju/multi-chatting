package com.example.joju.client;

public class ChatMessage {
    public boolean left;
    public String message;
    public String name = "";
    public String time;

    public ChatMessage(boolean left, String message, String time) {
        super();
        this.left = left;
        this.message = message;
        this.time = time;
    }

    public ChatMessage(boolean left, String message, String time, String name) {
        super();
        this.name = name;
        this.left = left;
        this.message = message;
        this.time = time;
    }
}
