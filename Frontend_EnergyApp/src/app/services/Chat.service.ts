import {Injectable} from '@angular/core';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {Observable} from 'rxjs';
import {MessageDto} from '../dtos/MessageDto';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private socket$: WebSocketSubject<MessageDto>;

  constructor() {
    this.socket$ = webSocket('ws://localhost:8085/chat');
  }

  sendMessage(message: MessageDto): void {
    console.log(message)
    this.socket$.next(message);
  }

  receiveMessages(): Observable<MessageDto> {
    return this.socket$.asObservable();
  }

  sendTypingNotification(sender: string, receiver: string): void {
    const typingNotification: MessageDto = {
      senderId: sender,
      receiverId: receiver,
      content: '',
      seen: false,
      typing: true,
    };
    this.sendMessage(typingNotification);
  }
}
