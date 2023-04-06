package cn.edu.sustech.cs209.chatting.common;

import cn.edu.sustech.cs209.chatting.common.Message;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatGroup {

    private final int id;
    private String name;
    private Set<String> users;
    private final List<Message> record;

    public ChatGroup(int id, String name) {
        this.id = id;
        this.name = name;
        users = new HashSet<>();
        record = new ArrayList<>();
    }

    public void addUser(String user) {
        users.add(user);
    }

    public void addUsers(List<String> users) {
        if (users != null) {
            this.users.addAll(users);
        }
    }

    public void addUsers(Set<String> users) {
        if (users != null) {
            this.users.addAll(users);
        }
    }

    public boolean containUser(String user){
        return users.contains(user);
    }

    public void addMessage(Message message) {
        record.add(message);
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
}
