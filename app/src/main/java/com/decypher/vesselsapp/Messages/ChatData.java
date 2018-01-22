package com.decypher.vesselsapp.Messages;

/**
 * Created by trebd on 10/28/2017.
 */

public class ChatData {
    public String chat_id, recipient, name, image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ChatData(String chat_id, String recipient, String name, String image) {
        this.chat_id=chat_id;
        this.recipient = recipient;
        this.image = image;

        this.name = name;

    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public ChatData(){}
}
