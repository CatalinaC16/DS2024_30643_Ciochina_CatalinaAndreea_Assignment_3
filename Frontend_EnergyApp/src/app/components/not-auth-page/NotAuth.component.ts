import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/Auth.service";
import {Router} from "@angular/router";
import {UserDto} from "../../dtos/UserDto";
import {UserService} from "../../services/User.service";
import {Role} from "../../dtos/Role";

@Component({
  selector: 'app-not-auth',
  templateUrl: './NotAuth.component.html',
  styleUrls: ['./NotAuth.component.css']
})
export class NotAuthComponent implements OnInit {

  userEmail: string = '';
  errorMessage: string = '';
  role!: Role;

  constructor(private authService: AuthService,
              private userService: UserService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser() {
    const token = this.authService.getToken();
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.userEmail = payload.sub;
      this.userService.getUserByEmail(this.userEmail).subscribe(
        (response: UserDto) => {
          this.role = response.role;
        },
        (error) => {
          this.errorMessage = 'Failed to load user';
          console.error(error);
        }
      );
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/login"]).then();
  }

  goToHomePage() {
    this.router.navigate(["/home"]).then();
  }
}
