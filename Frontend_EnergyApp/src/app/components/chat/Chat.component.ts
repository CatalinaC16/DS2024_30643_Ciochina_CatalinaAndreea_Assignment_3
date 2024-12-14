import {Component, OnInit} from '@angular/core';
import {ChatService} from "../../services/Chat.service";
import {MessageDto} from "../../dtos/MessageDto";
import {AuthService} from "../../services/Auth.service";
import {UserService} from "../../services/User.service";
import {UserDto} from "../../dtos/UserDto";
import {Router} from "@angular/router";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  chats: { user: string; messages: MessageDto[]; typing: boolean; newMessage: string }[] = [];
  currentUser: string = '';
  userToChatWith: string = '';
  isAdmin: boolean = false;
  newMessageNotifications: MessageDto[] = [];
  private typingTimers: { [key: string]: any } = {};
  usersToChatWith: UserDto[] = [];
  groupMessage: string = '';

  constructor(private chatService: ChatService,
              private authService: AuthService,
              private router: Router,
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
              if (message.receiverId === 'GROUP') {
                let groupChat = this.chats.find((c) => c.user === 'GROUP');
                if (!groupChat) {
                  groupChat = { user: 'GROUP', messages: [], typing: false, newMessage: '' };
                  this.chats.push(groupChat);
                }
                groupChat.messages.push(message);
              }else {
                const alreadyNotified = this.newMessageNotifications.find((n) => n.id === message.id);
                if (!alreadyNotified) {
                  this.newMessageNotifications.push(message);
                  this.openConversation(message);
                }
              }
            }
          });

          if (this.isAdmin) {
            this.loadUsers();
          } else {
            this.loadAdmins();
          }
        },
        (error) => {
          console.error(error);
        }
      );
    }
  }

  loadAdmins(): void {
    this.userService.getAllAdmins().subscribe(
      (admins) => {
        this.usersToChatWith = admins.filter((user) => user.id != this.currentUser)
      },
      (error) => {
        console.error('Error loading admins:', error);
      }
    );
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe(
      (admins) => {
        this.usersToChatWith = admins.filter((user) => user.id != this.currentUser)
      },
      (error) => {
        console.error('Error loading users:', error);
      }
    );
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

  sendGroupMessage(): void {
    if (!this.groupMessage.trim()) {
      console.warn('Cannot send an empty group message');
      return;
    }

    const groupMessage: MessageDto = {
      senderId: this.currentUser,
      receiverId: 'GROUP',
      content: this.groupMessage,
      seen: false,
    };

    this.chatService.sendMessage(groupMessage);
    let groupChat = this.chats.find((chat) => chat.user === 'GROUP');
    if (!groupChat) {
      groupChat = { user: 'GROUP', messages: [], typing: false, newMessage: '' };
      this.chats.push(groupChat);
    }
    groupChat.messages.push(groupMessage);
    this.groupMessage = '';
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
    const user = this.userToChatWith;
    if (!user) {
      console.warn('No user selected to start a chat.');
      return;
    }

    const existingChat = this.chats.find(chat => chat.user === user);

    if (existingChat) {
      console.log('Chat already exists with user:', user);
      return;
    }

    this.chats.push({ user, messages: [], typing: false, newMessage: '' });
    console.log('New chat started with user:', user);
  }

  openGroupChat(): void {
    let groupChat = this.chats.find(chat => chat.user === 'GROUP');
    if (!groupChat) {
      groupChat = {
        user: 'GROUP',
        messages: [],
        typing: false,
        newMessage: ''
      };
      this.chats.push(groupChat);
    }

    this.userToChatWith = 'GROUP';

    setTimeout(() => {
      const groupChatElement = document.querySelector('.chat-container .group-chat');
      if (groupChatElement) {
        groupChatElement.scrollIntoView({ behavior: 'smooth' });
      }
    }, 100);
  }


  logout(): void {
    this.authService.logout();
    this.router.navigate(["/login"]).then();
  }

  goToHomePage() {
    this.router.navigate(["/home"]).then();
  }

  viewUserDetails() {
    this.router.navigate(["/myProfile"]).then();
  }

  goToDevices() {
    this.router.navigate(["/myDevices"]).then();
  }

  goToAllDevices() {
    this.router.navigate(["/admin/all-devices"]).then();
  }

  goToAllUsers() {
    this.router.navigate(["/admin/all-users"]).then();
  }
}
