package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public class Request<T> implements Serializable {

  private final long timestamp;
  private RequestType type;
  private boolean isSuccess;
  private String info;
  private T obj;

  public Request(RequestType type, boolean isSuccess, String info, T obj) { // Server to Client
    this.timestamp = System.currentTimeMillis();
    this.type = type;
    this.isSuccess = isSuccess;
    this.info = info;
    this.obj = obj;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public RequestType getType() {
    return type;
  }

  public void setType(RequestType type) {
    this.type = type;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public T getObj() {
    return obj;
  }

  public void setObj(T obj) {
    this.obj = obj;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setSuccess(boolean success) {
    isSuccess = success;
  }
}
