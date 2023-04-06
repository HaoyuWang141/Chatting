package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public class Message implements Serializable {

    private Long timestamp;
    private String sentBy;
    private int sendTo;
    private String data;
    private MessageType type;

    public Message(Long timestamp, String sentBy, int sendTo, String data) {
        this.timestamp = timestamp;
        this.sentBy = sentBy;
        this.sendTo = sendTo;
        this.data = data;
        this.type = MessageType.Message;
    }

    public Message(Long timestamp, String sentBy, int sendTo, String data, MessageType type) {
        this.timestamp = timestamp;
        this.sentBy = sentBy;
        this.sendTo = sendTo;
        this.data = data;
        this.type = type;
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

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
