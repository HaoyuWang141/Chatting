package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;

public record LocalGroup(int id, String name, ChatGroupType type) implements Serializable {

}
