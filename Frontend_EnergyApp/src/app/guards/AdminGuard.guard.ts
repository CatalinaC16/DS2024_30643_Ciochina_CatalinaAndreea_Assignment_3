import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {UserService} from "../services/User.service";
import {Role} from "../dtos/Role";
import {AuthService} from "../services/Auth.service";
import {catchError, map, Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  role!: Role;

  constructor(private authService: AuthService,
              private userService: UserService,
              private router: Router) {
  }

  canActivate(): Observable<boolean> {
    const token = this.authService.getToken();

    if (!token) {
      this.router.navigate(['/login']);
      return of(false);
    }

    const payload = JSON.parse(atob(token.split('.')[1]));
    const userEmail = payload.sub;

    return this.userService.getUserByEmail(userEmail).pipe(
      map((response) => {
        const isAdmin = response.role === 'ADMIN';
        if (!isAdmin) {
          this.router.navigate(['/not-authorized']);
        }
        return isAdmin;
      }),
      catchError((error) => {
        console.error('Error fetching user role:', error);
        this.router.navigate(['/login']);
        return of(false);
      })
    );
  }
}
