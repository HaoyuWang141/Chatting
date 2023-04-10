# Chatting

> Author: Haoyu Wang (王浩羽)
>
> ID: 11911612

![CourseInfo](https://img.shields.io/badge/sustech--cs209-23sp%3Aassign2-brightgreen) ![License](https://img.shields.io/github/license/hezean/chatting)

**Chatting** is a simple online-chat application, based on JavaFX and Socket. In this assignment, you will need to implement a WeChat like program, but much simpler.

+ socket
+ multithreading
+ JavaFX



## Assignment Requirement

+ Server & Users (70 points)
  - [x] Server maintain a list of users that are currently connected to the Server
  - [x] Client can see a list of other users that are currently available to chat
  - [x] Client can select another user to start a one-to-one chat
  - [x] Client select multiple users to start a group chat
  - [x] Client can send text message
  - [ ] Client can send emojis
  
+ GUI (15 points)
  - [x] Use javafx to implement GUI
  - [x] Main panel to show a list of chats
  - [x] Chat room: a separate chatroom window created for the chat
  
+ Exception Handling (15 points)
  - [x] When one Client is offline, other Client will receive

+ Bonus (12 points)



## Quick Start

### Install the Project

We will define the commonly used constants and models in the `chatting-common` model,
which is the dependency of `chatting-client` and `chatting-server`.

> This design is a common practice in many large projects.
> But it is not mandatory for you to follow this architecture.
> You can move the model codes to other places if you want.

Now, the first thing you need to do is to install the parent pom into the local maven repository.

```shell
mvn install
```

If you are a Windows user who has not configured Maven environment variables, you can run this command in IDEA by several ways as below:

- double click the Ctrl key in IDEA to bring up a "Run Anything" pop-up window, then enter the command
- click the "Execute Maven Goal" button on the top of Maven side bar, then enter the command
- simply click on the "install" option in the "Lifecycle" folder of Maven side bar


<p align="center">
  <img  src="assets/mvn_command.jpg">
</p>


Note that each time after you modified the codes in `chatting-common`, you need to reinstall
the subproject -- you can think about why.

```shell
mvn install -pl chatting-common
```

### Run the Server

As our client will try to connect to the server socket when starting-up, you need to run the server before starting one or more clients.

Please find the `Main` class under the `chatting-server` model, implement your `ServerSocket`,
and run the `main` method.

### Run the Client

If you are using JDK 1.8 with JavaFX bundled, you may find the `Main` class under the `chatting-client` model, and run the `main` method to start a client. Note that you can start multiple clients by clicking the _run_ button several times.

If you are using JDK in any higher version, please use the `javafx` plugin to run the client.

```shell
mvn javafx:run -pl chatting-client
```

Alternatively, you can find the goal in the plugin list, and click on it:
<img src="assets/idea-maven-javafx-plugin.png" style="zoom:33%;" />



## Server

### properties

+ `clients`: a list that represents current online clients. 
+ `groupsMap`: record all chat group, including one-to-one and multi-user. A group uses Id to identify.



## User

### properties

+ `currentUsername`: username is unique
+ `onlineUserCnt`: current online user number(contain current user himself)
+ `userList`: other current online user list (not contain current user himself)
+ `chatGroupList` (id, group name): all of the current chat groups, contain one-to-one and multi-user chat group. 无论是一对一的聊天还是多人聊天，处理方式均相同，放在同一列表中
+ `currentChatId`: the id of current chat. 当前打开的聊天的id，使用id来确定唯一聊天，而不是聊天名字或群内用户
+ `chatContentList`: the content of current chat

### Operation

1. create one-to-one chat
   + select one user and click button "ok" to try to create one-to-one chat
     + if chat has existed in `chatGroupList` : set `currentChatId` 
     + else: send `CreateChatGroup Request` to Server, when get return message, set `currentChatId`
   + your chat content will focus on the one-to-one chat you want immediately.
2. create group chat
   + input group chat name, select several users and click button "ok" to create a multi-user chat
   + you can always create group chat successfully, including the same group name or the same group users
   +  send `CreateChatGroup Request` to Server, when get return message, set `currentChatId`
   +  your chat content will focus on the group chat you created immediately
3. select a chat
   + select a chat in `chatGroupList`, set  `currentChatId` 
   + update content when next "Request to Server"
4. send message
   + send a `Message` to Server
   + text can't be empty
5. Request to Server periodically
   + online user list (and number)
   + chat group list: other user may start a new chat with current user
   + chat content list: update chat content (1. other user may send new message; 2. current user may open another chat)



## Design

1. Info:

   + Client will only store some necessary info and update that from server periodically. For chat content, Client only maintain the current choose chat messages in local.

   + Server only send info to Client when a Client request specific info. Server maintain all chats and all message.



## Exception

1. Server is down: all user will be notified. User can quit by himself elegantly. User can only check the last chat messages. He can not create a new one-to-one chat or group chat.
2. a user is offline: Server will give all chats of this offline user a extra message to notify he is offline



## Difficulty

1. `ListView` 控件, 在被选中时, 会发生高频刷新闪烁问题: C#有double buffer的解决措施, 但是javafx没找到好办法
2. javafx的fxml文件编写较复杂: 采用图形化界面 *javafx Scene Builder* 进行界面构建, 非常高效.

2. 发表情: 尚未解决
3. 美化工作: 尚未解决



## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
