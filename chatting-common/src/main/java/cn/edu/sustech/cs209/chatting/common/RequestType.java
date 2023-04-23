package cn.edu.sustech.cs209.chatting.common;

public enum RequestType {
  Signup,
  Login,
  UserList, // 所有用户的列表
  ChatGroupList, // 当前用户的
  ChatContent,
  SendMessage,
  SendFile,
  CreateChatGroup,
  Disconnect
}
