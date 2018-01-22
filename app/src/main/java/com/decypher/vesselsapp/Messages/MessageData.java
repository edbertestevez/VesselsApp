package com.decypher.vesselsapp.Messages;

/**
 * Created by trebd on 10/28/2017.
 */

public class MessageData {
    public String chat_id, sender, receiver, message;
    public long time;

    public MessageData(String chat_id, String sender, String receiver, String message, long time) {
        this.chat_id=chat_id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public MessageData(){}
}
