package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.ChatGroup;
import cn.edu.sustech.cs209.chatting.common.ChatGroupType;
import cn.edu.sustech.cs209.chatting.common.User;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.ls.LSOutput;

public class test {

    public static void main(String[] args) {
        List<User> userList = new ArrayList<>();
        userList.add(new User("a", ""));
        userList.add(new User("b", ""));
        ChatGroup g1 = new ChatGroup(1, new User("a", ""), userList, ChatGroupType.OneToOneChat,
            "111");
        System.out.println(g1.containUser(new User("a", "")));
    }


}
