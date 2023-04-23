package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatGroup implements Serializable {

  private int id;
  private String name;
  private User owner;
  private final Map<User, Boolean> users;
  private ChatGroupType type;
  private final List<Message> messages;
  private final List<UploadedFile> files;
  private long lastActiveTime;

  public ChatGroup(int id, User owner, List<User> users, ChatGroupType type, String name) {
    this.id = id;
    this.owner = owner;
    this.users = new HashMap<>();
    this.addUsers(users);
    this.type = type;
    this.name = name;
    messages = new ArrayList<>();
    files = new ArrayList<>();
    lastActiveTime = -1;
  }

  public void addUser(User user) {
    if (user == null) {
      return;
    }
    users.put(user, false);
  }

  public void addUsers(List<User> userList) {
    if (userList == null) {
      return;
    }
    userList.forEach(e -> {
      users.put(e, false);
    });
  }

  public boolean containUser(User user) {
    for (User u : users.keySet()) {
      if (u.equals(user)) {
        return true;
      }
    }
    return false;
  }

  public void addMessage(Message message) {
    messages.add(message);
    if (message.getTimestamp() > lastActiveTime) {
      lastActiveTime = message.getTimestamp();
    }
    users.replaceAll((u, v) -> true);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public Set<User> getUsers() {
    return users.keySet();
  }

  public boolean oneToOneChatGroupEquals(ChatGroup chatGroup) {
    if (chatGroup == null) {
      return false;
    }
    if (!chatGroup.type.equals(ChatGroupType.OneToOneChat)
        || !this.type.equals(ChatGroupType.OneToOneChat)) {
      return false;
    }
    return this.users.keySet().containsAll(chatGroup.users.keySet()) && chatGroup.users.keySet()
        .containsAll(this.users.keySet());
  }

  public void setId(int id) {
    this.id = id;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public ChatGroupType getType() {
    return type;
  }

  public void setType(ChatGroupType type) {
    this.type = type;
  }

  public long getLastActiveTime() {
    return lastActiveTime;
  }

  public List<UploadedFile> getFiles() {
    return files;
  }

  public void addFile(UploadedFile file) {
    if (file == null) {
      return;
    }
    files.add(file);
  }

  public ChatContent getChatContent() {
    return new ChatContent(new ArrayList<>(messages), new ArrayList<>(users.keySet()),
        new ArrayList<>(files));
  }

  public void readNewMessage(User u) {
    if (u == null) {
      return;
    }
    users.put(u, false);
  }

  public boolean hasNewMessage(User u) {
    if (u == null) {
      return false;
    }
    return users.get(u);
  }
}
