import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {RegisterRequestDTO} from "../dtos/RegisterRequestDto";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private urlAPI = 'http://user.localhost/api/auth';

  constructor(private http: HttpClient) {
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.urlAPI}/login`, {email, password})
      .pipe(
        map((response: any) => {
          if (response && response.token) {
            localStorage.setItem('jwtToken', response.token);
          }
          return response;
        })
      );
  }

  register(registerRequestDTO: RegisterRequestDTO, byAdmin: boolean): Observable<any> {
    return this.http.post<any>(`${this.urlAPI}/register`, registerRequestDTO)
      .pipe(
        map((response: any) => {
          if (response && response.token && !byAdmin) {
            localStorage.setItem('jwtToken', response.token);
          }
          return response;
        })
      );
  }

  logout(): void {
    localStorage.removeItem('jwtToken');
  }

  public getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }
}
