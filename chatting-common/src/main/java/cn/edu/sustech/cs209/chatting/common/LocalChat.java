package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public record LocalChat(int id, String name, ChatGroupType type, boolean hasNewMessage) implements
    Serializable {

}
