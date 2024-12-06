import { Component, OnInit } from '@angular/core';
import { ChatService } from "../../services/Chat.service";
import { MessageDto } from "../../dtos/MessageDto";

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  chats: { user: string; messages: MessageDto[]; typing: boolean }[] = [];
  newMessage: string = '';

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    this.chatService.receiveMessages().subscribe((message) => {
      let chat = this.chats.find((c) => c.user === message.senderId || c.user === message.receiverId);
      if (!chat) {
        chat = { user: message.senderId, messages: [], typing: false };
        this.chats.push(chat);
      }
      if (message.typing) {
        chat.typing = true;
      } else {
        chat.typing = false;
        chat.messages.push(message);
      }
    });
  }

  sendMessage(receiver: string): void {
    let chat = this.chats.find((c) => c.user === receiver);
    if (!chat) {
      chat = { user: receiver, messages: [], typing: false };
      this.chats.push(chat);
    }

    this.chatService.sendMessage({
      senderId: 'currentUser',
      receiverId: receiver,
      content: this.newMessage,
      seen: false
    });

    chat.messages.push({
      senderId: 'currentUser',
      receiverId: receiver,
      content: this.newMessage,
      seen: false
    });

    this.newMessage = '';
  }

  startNewChat(): void {
    const user = prompt('Enter username of the person you want to chat with:');
    if (user) {
      this.chats.push({ user, messages: [], typing: false });
    }
  }
}
