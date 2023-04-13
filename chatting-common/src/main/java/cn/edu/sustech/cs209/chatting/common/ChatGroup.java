package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatGroup implements Serializable {

    private int id;
    private String name;
    private User owner;
    private Set<User> users;

    public ChatGroupType getType() {
        return type;
    }

    public void setType(ChatGroupType type) {
        this.type = type;
    }

    private ChatGroupType type;
    private final List<Message> record;
    private long lastActiveTime;


    public ChatGroup(int id, User owner, List<User> users, ChatGroupType type, String name) {
        this.id = id;
        this.owner = owner;
        this.users = new HashSet<>(users);
        this.type = type;
        this.name = name;
        record = new ArrayList<>();
        lastActiveTime = -1;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void addUsers(List<User> users) {
        if (users != null) {
            this.users.addAll(users);
        }
    }

    public void addUsers(Set<User> users) {
        if (users != null) {
            this.users.addAll(users);
        }
    }

    public boolean containUser(User user) {
        for (User u : users) {
            if (u.equals(user)) {
                return true;
            }
        }
        return false;
    }

    public void addMessage(Message message) {
        record.add(message);
        if (message.getTimestamp() > lastActiveTime) {
            lastActiveTime = message.getTimestamp();
        }
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

    public List<Message> getRecord() {
        return record;
    }

    public Set<User> getUsers() {
        return users;
    }

    public boolean oneToOneChatGroupEquals(ChatGroup chatGroup) {
        if (chatGroup == null) {
            return false;
        }
        if (!chatGroup.type.equals(ChatGroupType.OneToOneChat)
            || !this.type.equals(ChatGroupType.OneToOneChat)) {
            return false;
        }
        return this.users.containsAll(chatGroup.users) && chatGroup.users.containsAll(this.users);
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

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }
}
