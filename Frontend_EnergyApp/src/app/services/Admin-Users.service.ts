import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthService} from "./Auth.service";
import {Observable} from "rxjs";
import {UserDto} from "../dtos/UserDto";

@Injectable({
  providedIn: 'root'
})
export class AdminUsersService {
  private apiUrl = 'http://user.localhost/api/user';

  constructor(private http: HttpClient,
              private authService: AuthService) {
  }

  getAllUsers(): Observable<UserDto[]> {
    const headers = this.getHeaders();
    return this.http.get<UserDto[]>(`${this.apiUrl}/getAll`, {headers});
  }

  updateUser(userId: string, userDTO: UserDto): Observable<any> {
    const headers = this.getHeaders();
    return this.http.put<UserDto>(`${this.apiUrl}/update/${userId}`, userDTO, {headers});
  }

  deleteUser(userId: string): Observable<string> {
    const headers = this.getHeaders();
    return this.http.delete<string>(`${this.apiUrl}/deleteById/${userId}`, {
      headers: headers,
      responseType: 'text' as 'json'
    });
  }

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({'Authorization': `Bearer ${token}`});
  }
}
