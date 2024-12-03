import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { BehaviorSubject } from 'rxjs';
import { SocketMessageDto } from '../dtos/SocketMessageDto';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private wsUrl = 'ws://localhost:8082/ws';
  private socket$: WebSocketSubject<string> | null = null;

  public alertsSubject = new BehaviorSubject<SocketMessageDto[]>([]);
  public alerts$ = this.alertsSubject.asObservable();

  connect() {
    if (!this.socket$ || this.socket$.closed) {
      this.socket$ = webSocket(this.wsUrl);

      this.socket$.subscribe(
        (message) => {
          try {
            const parsedMessage: string = JSON.stringify(message);
            let alert = JSON.parse(parsedMessage)
            this.addAlert(alert);
          } catch (error) {
            console.error('Error parsing WebSocket message:', error);
          }
        },
        (error) => {
          console.error('WebSocket error:', error);
        },
        () => {
          console.log('WebSocket connection closed');
        }
      );
    }
  }

  private addAlert(alert: SocketMessageDto) {
    const currentAlerts = this.alertsSubject.getValue();
    this.alertsSubject.next([...currentAlerts, alert]);
  }

  disconnect() {
    this.socket$?.complete();
    console.log('WebSocket disconnected');
  }
}
