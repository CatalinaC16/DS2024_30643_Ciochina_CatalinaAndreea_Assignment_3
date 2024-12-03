import {Router} from "@angular/router";
import {Component} from "@angular/core";
import {AuthService} from "../../services/Auth.service";
import {RegisterRequestDTO} from "../../dtos/RegisterRequestDto";

@Component({
  selector: 'app-auth',
  templateUrl: './Auth.component.html',
  styleUrls: ['./Auth.component.css']
})
export class AuthComponent {
  firstName = '';
  secondName = '';
  email = '';
  password = '';
  isErrorMessage: boolean = false;
  isRegistering: boolean = false;

  constructor(private authService: AuthService, private router: Router) {
  }

  onLoginUser(): void {
    this.authService.login(this.email, this.password).subscribe(
      () => {
        this.router.navigate(['/home']);
      },
      (error: any) => {
        this.isErrorMessage = true;
        console.error('Login failed', error);
      }
    );
  }

  onRegisterUser(): void {
    const registerRequestDTO: RegisterRequestDTO = {
      firstName: this.firstName,
      secondName: this.secondName,
      email: this.email,
      password: this.password
    };

    this.authService.register(registerRequestDTO,false).subscribe(
      () => {
        this.isErrorMessage = false;
        this.router.navigate(['/home']);
      },
      (error: any) => {
        this.isErrorMessage = true;
        console.error('Registration failed', error);
      }
    );
  }

  toggleForm(): void {
    this.isRegistering = !this.isRegistering;
    this.firstName = '';
    this.secondName = '';
    this.email = '';
    this.password = '';
    this.isErrorMessage = false;
  }
}
