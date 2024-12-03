import {Component, OnInit} from '@angular/core';
import {UserDto} from "../../dtos/UserDto";
import {AdminUsersService} from "../../services/Admin-Users.service";
import {Router} from "@angular/router";
import {AuthService} from "../../services/Auth.service";
import {RegisterRequestDTO} from "../../dtos/RegisterRequestDto";

@Component({
  selector: 'app-admin-users',
  templateUrl: './AdminUsers.component.html',
  styleUrls: ['./AdminUsers.component.css']
})
export class AdminUsersComponent implements OnInit {
  users: UserDto[] = [];
  errorMessage = '';
  editMode = false;
  createMode = false;
  selectedUser: UserDto | null = null;
  firstName = '';
  secondName = '';
  email = '';
  password = '';

  constructor(private adminService: AdminUsersService,
              private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.getAllUsers();
  }

  getAllUsers() {
    this.adminService.getAllUsers().subscribe(
      (data) => (this.users = data),
      (error) => {
        this.errorMessage = 'Sorry, could not load users.';
        console.error('Error fetching users:', error);
      }
    );
  }

  selectUser(user: UserDto) {
    this.selectedUser = {...user};
    this.editMode = true;
  }

  createUser(){
    this.createMode = true;
  }

  onCreateUser(): void {
    const registerRequestDTO: RegisterRequestDTO = {
      firstName: this.firstName,
      secondName: this.secondName,
      email: this.email,
      password: this.password
    };
    this.authService.register(registerRequestDTO,true).subscribe(() => {
      this.getAllUsers();
      this.createMode = false;
    });
  }

  updateUser() {
    if (this.selectedUser) {
      this.adminService.updateUser(this.selectedUser.id, this.selectedUser).subscribe(
        () => {
          this.editMode = false;
          this.getAllUsers();
        },
        (error) => console.error('Error updating user:', error)
      );
    }
  }

  deleteUser(userId: string) {
    if (confirm('Are you sure you want to delete this user?')) {
      this.adminService.deleteUser(userId).subscribe(
        () => this.getAllUsers(),
        (error) => console.error('Error deleting user:', error)
      );
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/login"]).then();
  }

  viewUserDetails() {
    this.router.navigate(["/myProfile"]).then();
  }

  goToHomePage() {
    this.router.navigate(["/home"]).then();
  }

  goToAllDevices() {
    this.router.navigate(["/admin/all-devices"]).then();
  }

  goToAllUsers() {
    this.router.navigate(["/admin/all-users"]).then();
  }
}
