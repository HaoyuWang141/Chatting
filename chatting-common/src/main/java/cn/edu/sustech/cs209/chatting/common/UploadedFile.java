package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public class UploadedFile implements Serializable {

    private final long timestamp;
    private String name;
    private String data;
    private String sendBy;
    private int sendTo;

    public UploadedFile(String name, String data, String sendBy, int sendTo) {
        timestamp = System.currentTimeMillis();
        this.name = name;
        this.data = data;
        this.sendBy = sendBy;
        this.sendTo = sendTo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }

    public int getSendTo() {
        return sendTo;
    }

    public void setSendTo(int sendTo) {
        this.sendTo = sendTo;
    }
}
