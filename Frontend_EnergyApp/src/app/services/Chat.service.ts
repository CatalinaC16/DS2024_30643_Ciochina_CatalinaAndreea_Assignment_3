import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable, Subject } from 'rxjs';
import { MessageDto } from '../dtos/MessageDto';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './Auth.service';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private socket$: WebSocketSubject<MessageDto> | null = null;
  private urlChatMs = 'http://localhost:8085/api/messages';
  public newMessageNotificationSubject = new Subject<MessageDto>(); // pentru notificarea mesajului nou

  constructor(private http: HttpClient, private authService: AuthService) {}

  // Conectează utilizatorul la WebSocket
  connect(userId: string): void {
    const url = `ws://localhost:8085/chat?userId=${userId}`;
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = webSocket<MessageDto>(url);
    }

    this.socket$.subscribe(
      (message: MessageDto) => {
        this.newMessageNotificationSubject.next(message); // Emit notificare pentru mesaje noi
      },
      (err) => console.error('WebSocket error:', err)
    );
  }

  // Trimite un mesaj
  sendMessage(message: MessageDto): void {
    if (this.socket$) {
      console.log('Sending message:', message);
      this.socket$.next(message);
    } else {
      console.error('WebSocket is not connected. Message not sent.');
    }
  }

  // Trimite o notificare de "typing"
  sendTypingNotification(senderId: string, receiverId: string): void {
    if (this.socket$) {
      const typingMessage: MessageDto = {
        senderId: senderId,
        receiverId: receiverId,
        content: '',
        seen: false,
        typing: true
      };
      this.socket$.next(typingMessage);
    }
  }

  // Abonare la mesajele primite
  receiveMessages(): Observable<MessageDto> {
    if (this.socket$) {
      return this.socket$.asObservable();
    } else {
      throw new Error('WebSocket is not connected.');
    }
  }

  // Deconectare WebSocket
  disconnect(): void {
    if (this.socket$) {
      this.socket$.complete();
      this.socket$ = null;
      console.log('WebSocket connection closed.');
    }
  }
}
