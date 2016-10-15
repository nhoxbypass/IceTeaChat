package com.gdg.firebase.iceteachat.Model;

/**
 * Created by Xa Tao Ra on 3/11/2016.
 */
public class ChatMessage {
    private String sender;
    private String text;

    public ChatMessage() {
        // necessary for Firebase's deserializer
    }
    public ChatMessage(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

}
