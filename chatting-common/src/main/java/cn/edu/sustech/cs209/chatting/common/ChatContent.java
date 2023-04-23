package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.List;

public record ChatContent(List<Message> messages, List<User> users,
                          List<UploadedFile> files) implements Serializable {

}
