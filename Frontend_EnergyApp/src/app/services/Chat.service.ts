import {Injectable} from '@angular/core';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {Observable, Subject} from 'rxjs';
import {MessageDto} from '../dtos/MessageDto';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private socket$: WebSocketSubject<MessageDto> | null = null;
  public newMessageNotificationSubject = new Subject<MessageDto>();

  connect(userId: string): void {
    const url = `wss://message.localhost/chat?userId=${userId}`;
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = webSocket<MessageDto>(url);
    }

    this.socket$.subscribe(
      (message: MessageDto) => {
        console.log('Receiving message:', message)
        this.newMessageNotificationSubject.next(message);
      },
      (err) => console.error('WebSocket error:', err)
    );
  }

  sendMessage(message: MessageDto): void {
    if (this.socket$) {
      console.log('Sending message:', message);
      this.socket$.next(message);
    } else {
      console.error('WebSocket is not connected. Message not sent.');
    }
  }

  sendTypingNotification(senderId: string, receiverId: string, typing:boolean): void {
    if (this.socket$) {
      const typingMessage: MessageDto = {
        senderId: senderId,
        receiverId: receiverId,
        content: '',
        seen: false,
        typing: typing
      };
      this.socket$.next(typingMessage);
    }
  }

  markMessagesAsSeen(messages: MessageDto[]): void {
    if (this.socket$) {
      messages.forEach((message) => {
        const seenNotification: MessageDto = {
          ...message,
          seen: true,
          content: '',
        };
        console.log('Sending seen notification:', seenNotification);
        this.socket$!.next(seenNotification);
      });
    } else {
      console.error('WebSocket is not connected. Seen notification not sent.');
    }
  }

  disconnect(): void {
    if (this.socket$) {
      this.socket$.complete();
      this.socket$ = null;
      console.log('WebSocket connection closed.');
    }
  }
}
