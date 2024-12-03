import {Component, OnInit} from '@angular/core';
import {UserDto} from "../../dtos/UserDto";
import {UserService} from "../../services/User.service";
import {AuthService} from "../../services/Auth.service";
import {Router} from "@angular/router";
import {NgForm} from '@angular/forms';
import {Role} from "../../dtos/Role";

@Component({
  selector: 'app-profile',
  templateUrl: './Profile.component.html',
  styleUrls: ['./Profile.component.css']
})
export class ProfileComponent implements OnInit {
  user: UserDto | null = null;
  errorMessage: string = '';
  password = '';
  firstname = '';
  secondname = '';
  email = '';
  id = '';
  role!: Role;

  constructor(private userService: UserService,
              private router: Router,
              private authService: AuthService) {
  }

  ngOnInit() {
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
          this.user = response;
          this.id = response.id;
          this.firstname = response.firstName;
          this.secondname = response.secondName;
          this.email = response.email;
          this.role = response.role;
        },
        (error) => {
          this.errorMessage = 'Failed to load user';
          console.error(error);
        }
      );
    }
  }

  onSave(form: NgForm) {
    if (form.valid) {
      const updatedUser: UserDto = new UserDto(this.id, this.firstname, this.secondname, this.email, this.role);
      this.userService.updateUser(this.id, updatedUser).subscribe({
        next: (response) => {
          console.log('User updated successfully', response);
          this.logout();
        },
        error: (error) => {
          console.error('Error updating user:', error);
          alert('Failed to update profile. Please try again.');
        }
      });
    } else {
      alert('Please fill out the form correctly.');
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/login"]).then();
  }

  goToHomePage() {
    this.router.navigate(["/home"]).then();
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
