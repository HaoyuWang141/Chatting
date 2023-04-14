# Chatting

> Author: Haoyu Wang (王浩羽)
>
> ID: 11911612

![CourseInfo](https://img.shields.io/badge/sustech--cs209-23sp%3Aassign2-brightgreen) ![License](https://img.shields.io/github/license/hezean/chatting)

**Chatting** is a simple online-chat application, based on JavaFX and Socket. In this assignment, you will need to implement a WeChat like program, but much simpler.

## Exhibition

<img src="img\GUI\exhibition1.png" alt="exhibition1" width=70% />

<img src="img\GUI\exhibition2.png" alt="exhibition2" width=70% />

## Introduction

+ Server: multithreading + thread pool: 每个线程负责连接一个Client请求
+ Client GUI: JavaFX - 前后端分离: 一个fxml文件对应于一个独立的Controller程序.
  + `fxml`文件为静态文件, 包含GUI的全部组件
  + `Controller`程序为控制层, 负责处理Client后端和前端GUI的交互逻辑
+ Client 后端: 
  + `Client` 类: 该类独立于控制层存在, 其功能包括
    + 连接服务器
    + 发送请求
    + 发送消息
    + 接受信息并处理
    + 安全关闭客户端并与服务器断开连接
  + multithreading: `Client`类中一个专门的线程负责随时接收Server发送至Client的信息, 并根据消息类型及内容转发给各控制层内的Controller或调用Controller的方法. 其中, 消息的转发使用`Pipe Input/Output Stream`进行线程间的通讯.
+ Communication: 将socket的`Input/Output Stream`传入`Object Input/Output Stream`进行通信, 通过对象序列化的方式转递信息.



## Assignment Requirement

+ Server & Users (70 points)
  - [x] Server maintain a list of users that are currently connected to the Server
  - [x] Client can see a list of other users that are currently available to chat
  - [x] Client can select another user to start a one-to-one chat
  - [x] Client select multiple users to start a group chat
  - [x] Client can send text messages and receive the messages
  - [x] Client can send emojis
+ GUI (15 points)
  - [x] Use javafx to implement GUI
  - [x] Main panel to show a list of chats
  - [x] Chat room: a separate chatroom window created for the chat
+ Exception Handling (15 points)
  - [x] When one Client is offline, other clients that have chat with the offline client will receive one  Server message in his chat content that notifies the client is offline.
  - [x] When Server is offline, all online clients will get a dialog window that notifies the Server is offline. Then the clients can check only history messages of the last chat. He can not begin a one-to-one chat or a group chat.
+ Bonus (12 points)
  - [x] Account Management: the server could support user registration and login.
  - [x] Sorted Chat List: user could see a list of available chats, sorted by their recent chat activities
  - [x] Chat History: client can get the chat history.
  - [ ] File Transfer



## Quick Start

### Install the Project

We will define the commonly used constants and models in the `chatting-common` model,
which is the dependency of `chatting-client` and `chatting-server`.

Now, the first thing you need to do is to install the parent pom into the local maven repository.

```shell
mvn install
```

If you are a Windows user who has not configured Maven environment variables, you can run this command in IDEA by several ways as below:

- double click the Ctrl key in IDEA to bring up a "Run Anything" pop-up window, then enter the command
- click the "Execute Maven Goal" button on the top of Maven side bar, then enter the command
- simply click on the "install" option in the "Lifecycle" folder of Maven side bar

<p align="center">
  <img  src="img/assets/mvn_command.jpg" width=30%>
</p>




Each time after modifing the codes in `chatting-common`, subproject needs to be reinstalled.

```shell
mvn install -pl chatting-common
```

### Run the Server

Ffind the `Main` class under the `chatting-server` model. Run the `main` method.

### Run the Client

If you are using JDK 1.8 with JavaFX bundled, you may find the `Main` class under the `chatting-client` model, and run the `main` method to start a client. Note that you can start multiple clients by clicking the _run_ button several times.

If you are using JDK in any higher version, please use the `javafx` plugin to run the client.

```shell
mvn javafx:run -pl chatting-client
```

Alternatively, you can find the goal in the plugin list, and click on it:

<img src="img/assets/idea-maven-javafx-plugin.png" width=30% />



## Server

### properties

+ `clients`: a list that represents current online clients. 
+ `groupsMap`: record all chat group, including one-to-one and multi-user. A group uses ID to identify. A chat group object mainly have id, name and maintain a message list.

### Behavior

Server receives the **Request** or **Message** from a Client, records messary info and returns a response to the Client.



## Client

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

### Exceptions and Functions

#### Connect

1. Connecting Server is failed

   <img src="img\C-connectFailed.png" alt="C-connectFailed" width=50%; />

#### Login

1. username or password is empty

   <img src="img\L-emptyWrong.png" alt="L-emptyWrong" width=50%; />

2. username does not exist

   <img src="img\L-userNotExist.png" alt="L-userNotExist" width=50%; />

3. password is wrong

   <img src="img\L-pwdWrong.png" alt="L-pwdWrong" width=50%; />

4. user has already been online

   <img src="img\L-userOnlineWrong.png" alt="L-userOnlineWrong" width=50%; />

5. if use can login, he will go to the Chat GUI:

   <img src="img\GUI\chatGUI.png" alt="chatGUI" width=70% />

#### Signup

1. username has already exists

   <img src="img\S-userExistWrong.png" alt="S-userExistWrong" width=50%; />

2. signup successfully

   <img src="img\S-Success.png" alt="S-Success" width=50%; />

#### emoji

<img src="img\GUI\emoji.png" alt="emoji" width=70% />

#### sorted chat list

1. User a send a message "A" in group chat xxx, then xxx will be the first chat in the chat list:

<img src="img\GUI\sortedChat1.png" alt="sortedChat1" width=70% />

2. Then user a send another message "B" in one-to-one chat with user b. This private chat becomes the last active chat and be the first chat.

<img src="img\GUI\sortedChat2.png" alt="sortedChat2" width=70% />

#### chat history

1. User a is offline

<img src="img\GUI\chatHistory1.png" alt="chatHistory1" width=70% />

2. User a is online again, and he can get history messages.

<img src="img\GUI\chhatHistory2.png" alt="chhatHistory2" width=70% />

## Design

1. `Message`对象: 该对象专门用于储存一条消息, 其属性包含:
   + timestamp: 时间戳, 记录发出消息时的时间
   + sentBy: String类型, 发送者. 可为某个用户的名称或"Server"
   + sendTo: int类型, 表示想要发送到的聊天id, 每一个Server中维护的聊天都用id唯一表示
   + data: 信息内容
2. 将私聊和群聊视为近乎相同的对象, 称为`ChatGroup`. 它仅通过一个属性加以区分私聊和群聊, 而在行为上没有区别. 所有`ChatGroup`都会被维护在服务器端. 当用户向服务器申请开通任意一类聊天时, 服务器端将创建一个新的`ChatGroup`对象, 该对象包含属性如下:
   + id: 用以唯一确定一个聊天
   + name: 对于群聊, 储存自定义的群聊名称; 对于私聊, 不储存内容
   + owner: 群主, 即申请创建聊天的用户
   + users: 参与该聊天的用户列表
   + type: 该聊天的类型时私聊或群聊
   + record: 消息记录, 以`Message`对象储存
   + lastActiveTime: 最后一个消息的时间, 用以在用户界面以聊天活跃时间为顺序进行排序

3. 请求与回应: 为完成聊天功能, 客户端将根据需求向服务器端发送一系列的请求, 服务器收到请求后会做出回应. 整个过程通过java socket完成. 建立连接后, 客户端可能发出的请求类型及对应的回应分为以下数种:

   + signup: 申请注册

   + login: 申请登录

   + UserList: 申请在线用户列表. 服务器将返回所有在线用户列表

   + ChatGroupList: 申请本用户所在的全部聊天(包括私聊和群聊), 服务器将以`LocalGroup`的数据类型返回聊天列表. `LocalGroup`仅包含以下属性:

     + id: 识别聊天的唯一标识符, 在用户向某个聊天发送消息时, 将指定该参数

     + name: 用于显示在GUI上的聊天名称, 私聊将显示另一个用户的名称, 群聊直接显示群名

     + type: 用于辨别该聊天是私聊还是群聊

     服务器在发送`LocalGroup`列表时, 将会把列表的顺序以及这三个属性均维护正确. 

     只传递一个聊天的部分信息而不是完整信息的原因是: 用户每次都会请求全部聊天列表, 若此时将全部聊天的全部聊天记录发送, 会传递大量数据且很多数据是冗余的. 因此, 用户维护聊天信息的部分, 并不在该请求中实现.

   + MessageList: 申请给定聊天id的全部聊天内容. 服务器将返回`Message`列表

   + CreateChatGroup: 申请开一个新的聊天(包括私聊和群聊), 服务器会判断是否允许开启新聊天 (开群聊没有限制, 可以开重名群聊, 也可以开多个包含相同用户的群聊; 开私聊的情况, 两个用户之间仅能存在一个私聊). 最终, 服务器返回一个聊天id (新聊天id或已存在的老聊天id).

   + Disconnect: 通知服务器断开连接, 服务器将安全地回收各种资源, 维护用户列表, 通知各群聊或其他用户该用户下线.

   为保证客户端始终能够获取最新的数据, 本工程的实现方式为, 客户端周期性地向服务器发出UserList, ChatGroupList, MessageList三个申请, 并将服务器返回的信息展示在GUI上.

4. 线程间通信: 用户接受消息的线程和javafx的线程之间常常需要进行通信, 这里根据需求采用了两种方式: 

   + 直接转发收到的信息: 采用java中的`Pipe Input/Output Stream`直接将收到的信息转发给指定线程, 在Connect, Signup, Login阶段多采用这种方式.
   + 调用其他线程的方法: java多线程中, 对象是可以共享的, 因此只要维护好共享对象, 就可以直接调用该对象中需要的方法, 在Chat阶段采用该方式.



## Exception

处理在使用过程中, 因为网络问题所导致的一些异常. 期望在这些异常发生时, 程序可以优雅地将它们反馈给用户. 避免程序突然退出, 或者爆出很多红色地异常提示.

1. One Client is offline: Server will give all chats of this offline user a extra message to notify he is offline

   <img src="img\O-offline.png" alt="O-offline" width=70%; />

   If the offline client is online again, all his chat will get an extra message from Server: "User 'x' is online":

   <img src="img\O-onlineAgain.png" alt="O-onlineAgain" width=70%; />

2. Server is down: all user will be notified. User can quit by himself elegantly. User can only check the last chat messages. He can not create a new one-to-one chat or group chat.

   <img src="img\SO-serverOffline.png" alt="SO-serverOffline" width=70%; />

   If Client sends message, he will get:

   <img src="img\SO-send.png" alt="SO-send" width=70%; />

   And Online User is set to be zero.



## Difficulty

1. `ListView` 控件, 在被选中时, 会发生高频刷新闪烁问题: C#有double buffer的解决措施, 但是javafx没找到好办法. 暂且使用0.5s进行一次刷新的方式缓解.

2. javafx的fxml文件编写较复杂: 采用图形化界面 *javafx Scene Builder* 进行界面构建, 非常高效.

3. 客户端GUI界面关闭后, 该用户仍然在连接着服务器, 原因是仍有未处理的线程占用系统资源或占用端口: 在类`Client`中增加`close()`方法, 当负责GUI界面的线程关闭时, 需要执行该`close()`方法, 将其他必要的线程也全部终止, 并向Server发出`Disconnect`请求.

4. 回车发送消息：[JavaFX 实现回车发送信息，Ctrl+Enter换行](https://blog.csdn.net/wangpaiblog/article/details/121506912)

   为TextArea添加key press监听器, 当收到Enter后, 将判定发送消息或换行.

5. 发表情: 使用依赖emoji-java处理表情, 非常方便.

   [emoji-java: The missing emoji library for Java](https://github.com/vdurmont/emoji-java)

   [轻量级工具emoji-java处理emoji表情字符](https://blog.csdn.net/qq_44799924/article/details/117114788)

6. 发文件: 尚未解决

7. 其他设计思路及优化:

   + 对于私聊, 在两个用户之间建立管道进行通讯. 这种方式有更快的通讯速度和安全性, 即使服务器因为请求多而迟缓, 私聊的通讯也可以不受影响;
   + 客户端在本地储存全部群聊的聊天记录, 而不是仅当前群聊的聊天记录. 这样在离线时, 客户端仍然可以查看所有的聊天记录. 但这可能涉及到了各个用户端聊天记录与服务器端的同步问题.
   + 目前的设计中, 在更新聊天记录时, 服务端会发送全部聊天记录. 可以通过客户端申请聊天记录时, 增加参数时间戳, 表示仅申请该时间后的聊天记录, 服务器将发送这一小部分聊天记录, 效率更高.



## GUI Color Design

<img src="img\GUI\color.jpg" alt="color" width=80% />



## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
