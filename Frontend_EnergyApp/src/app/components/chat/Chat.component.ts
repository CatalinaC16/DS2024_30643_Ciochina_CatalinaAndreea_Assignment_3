import {Component, OnInit} from '@angular/core';
import {ChatService} from "../../services/Chat.service";
import {MessageDto} from "../../dtos/MessageDto";
import {AuthService} from "../../services/Auth.service";
import {UserService} from "../../services/User.service";
import {UserDto} from "../../dtos/UserDto";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  chats: { user: string; messages: MessageDto[]; typing: boolean; newMessage: string }[] = [];
  newMessage: string = '';
  currentUser: string = '';
  adminId: string = 'bc5f4afc-8990-423a-8e23-a5cabb9ef24a';
  adminName: string = "Admin";
  currentUserRole: string = '';
  isAdmin: boolean = false;
  newMessageNotifications: MessageDto[] = [];

  constructor(private chatService: ChatService,
              private authService: AuthService,
              private userService: UserService) {
  }

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser() {
    const token = this.authService.getToken();
    let userEmail: string;
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      userEmail = payload.sub;
      this.userService.getUserByEmail(userEmail).subscribe(
        (response: UserDto) => {
          this.currentUser = response.id;
          this.isAdmin = response.role === 'ADMIN';
          this.chatService.connect(this.currentUser);
          this.loadAdminChats();

          this.chatService.newMessageNotificationSubject.subscribe((message) => {
            if (message.typing) {
              this.updateTypingStatus(message.senderId, true);
            } else {
              let chat = this.chats.find(c => c.user === message.senderId || c.user === message.receiverId);
              if (!chat) {
                chat = {user: message.senderId, messages: [], typing: false, newMessage: ''};
                this.chats.push(chat);
              }
              chat.messages.push(message);
              chat.typing = false;
            }
          });

        },
        (error) => {
          console.error(error);
        }
      );
    }
  }

  loadAdminChats() {
    if (this.isAdmin) {

    }
  }

  sendMessage(receiver: string): void {
    let chat = this.chats.find((c) => c.user === receiver);
    if (!chat) {
      chat = { user: receiver, messages: [], typing: false, newMessage: '' };
      this.chats.push(chat);
    }

    if (!chat.newMessage.trim()) {
      console.warn('Cannot send an empty message');
      return;
    }

    const message: MessageDto = {
      senderId: this.currentUser,
      receiverId: receiver,
      content: chat.newMessage,
      seen: false,
    };

    this.chatService.sendMessage(message);
    chat.messages.push(message);
    chat.newMessage = '';
  }

  sendTypingNotification(receiver: string): void {
    this.chatService.sendTypingNotification(this.currentUser, receiver);
  }

  updateTypingStatus(senderId: string, typing: boolean) {
    let chat = this.chats.find(c => c.user === senderId);
    if (chat) {
      chat.typing = typing;
    }
  }

  updateMessageStatus(message: MessageDto) {
    let chat = this.chats.find(c => c.user === message.senderId);
    if (chat) {
      let msg = chat.messages.find(m => m.senderId === message.senderId && m.content === message.content);
      if (msg) {
        msg.seen = true;
      }
    }
  }

  openConversation(notification: MessageDto): void {
    let chat = this.chats.find((c) => c.user === notification.senderId);
    if (!chat) {
      chat = {user: notification.senderId, messages: [], typing: false, newMessage: ''};
      this.chats.push(chat);
    }
    chat.messages.push(notification);

    this.newMessageNotifications = this.newMessageNotifications.filter(n => n !== notification);
  }


  startNewChat(): void {
    const user = this.adminId;
    if (user) {
      this.chats.push({user, messages: [], typing: false, newMessage: ''});
    }
  }
}
