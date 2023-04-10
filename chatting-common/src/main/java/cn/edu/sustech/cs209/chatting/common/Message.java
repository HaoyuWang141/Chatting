package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public class Message implements Serializable {

    private final Long timestamp;
    private String sentBy;
    private int sendTo;
    private String data;

    public Message() {
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String sentBy, int sendTo, String data) {
        this.timestamp = System.currentTimeMillis();
        this.sentBy = sentBy;
        this.sendTo = sendTo;
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getSentBy() {
        return sentBy;
    }

    public int getSendTo() {
        return sendTo;
    }

    public String getData() {
        return data;
    }

}
