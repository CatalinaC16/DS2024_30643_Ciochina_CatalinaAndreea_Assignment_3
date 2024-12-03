import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "./services/Auth.service";
import {UserService} from "./services/User.service";
import {WebSocketService} from "./services/Web-Soket.service";
import {UserDto} from "./dtos/UserDto";
import {SocketMessageDto} from "./dtos/SocketMessageDto";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Frontend_EnergyApp';
  alerts: SocketMessageDto[] = [];
  errorMessage: string = '';
  userEmail: string = '';
  userDto!: UserDto;

  constructor(private authService: AuthService,
              private userService: UserService,
              private webSocketService: WebSocketService) {
  }

  ngOnInit(): void {
    this.loadUser();
    this.webSocketService.connect();
    this.webSocketService.alerts$.subscribe((alerts) => {
      this.alerts = alerts.filter((alert) => alert.userId === this.userDto.id);
    });

  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }

  loadUser() {
    const token = this.authService.getToken();
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.userEmail = payload.sub;
      this.userService.getUserByEmail(this.userEmail).subscribe(
        (response: UserDto) => {
          this.userDto = response;
        },
        (error) => {
          this.errorMessage = 'Failed to load user';
          console.error(error);
        }
      );
    }
  }

  closeAlert(index: number) {
    const removedAlert = this.alerts.splice(index, 1)[0];
    const updatedAlerts = this.webSocketService.alertsSubject.getValue().filter((alert) => alert !== removedAlert);
    this.webSocketService.alertsSubject.next(updatedAlerts);
  }

}
