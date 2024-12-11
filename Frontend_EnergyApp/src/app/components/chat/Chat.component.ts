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
  currentUser: string = '';
  adminId: string = 'bc5f4afc-8990-423a-8e23-a5cabb9ef24a';
  isAdmin: boolean = false;
  newMessageNotifications: MessageDto[] = [];
  private typingTimers: { [key: string]: any } = {};

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
            if (message.typing || message.content === '') {
              this.updateTypingStatus(message.senderId, message.typing!);
            } else if (message.seen) {
              console.log("Seen notification for message:", message.id);
              this.updateMessageStatus(message);
            } else {
              const alreadyNotified = this.newMessageNotifications.find((n) => n.id === message.id);
              if (!alreadyNotified) {
                this.newMessageNotifications.push(message);
                this.openConversation(message);
              }

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
      chat = {user: receiver, messages: [], typing: false, newMessage: ''};
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
    if (this.chatService) {
      this.chatService.sendTypingNotification(this.currentUser, receiver, true);
      if (this.typingTimers[receiver]) {
        clearTimeout(this.typingTimers[receiver]);
      }

      this.typingTimers[receiver] = setTimeout(() => {
        this.sendStopTypingNotification(receiver);
      }, 3000);
    }
  }

  sendStopTypingNotification(receiver: string): void {
    if (this.chatService) {
      this.chatService.sendTypingNotification(this.currentUser, receiver, false);
    }
  }

  updateTypingStatus(senderId: string, typing: boolean): void {
    const chat = this.chats.find(c => c.user === senderId);
    if (chat) {
      chat.typing = typing;
    }
  }

  updateMessageStatus(message: MessageDto) {
    const chat = this.chats.find(c => c.user === message.receiverId);
    if (chat) {
      chat.messages.forEach((msg) => {
        if (!msg.seen) {
          msg.seen = true;
        }
      });
    }
  }

  openConversation(notification: MessageDto): void {
    let chat = this.chats.find((c) => c.user === notification.senderId);
    if (!chat) {
      chat = {user: notification.senderId, messages: [], typing: false, newMessage: ''};
      this.chats.push(chat);
    }

    chat.messages.push(notification);

    const unseenMessages = chat.messages.filter((m) => !m.seen && m.senderId !== this.currentUser);

    if (unseenMessages.length > 0) {
      this.chatService.markMessagesAsSeen(unseenMessages);
      unseenMessages.forEach((m) => (m.seen = true));
    }

    this.newMessageNotifications = this.newMessageNotifications.filter((n) => n.id !== notification.id);
  }


  startNewChat(): void {
    const user = this.adminId;
    if (user) {
      this.chats.push({user, messages: [], typing: false, newMessage: ''});
    }
  }
}
