package com.gdg.firebase.iceteachat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Xa Tao Ra on 3/11/2016.
 */
public class ChatMessage {

    @JsonIgnore
    private String id;
    private String sender;
    private String text;
    private String photoUrl;

    public ChatMessage() {
        // necessary for Firebase's deserializer
    }
    public ChatMessage(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

}
