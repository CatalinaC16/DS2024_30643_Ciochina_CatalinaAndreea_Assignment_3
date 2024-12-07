import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthService} from "./Auth.service";
import {map, Observable} from "rxjs";
import {UserDto} from "../dtos/UserDto";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private urlAPI = 'http://localhost:8084/api/user';

  constructor(private http: HttpClient,
              private authService: AuthService) {
  }

  getUserByEmail(email: string): Observable<UserDto> {
    const headers = this.getHeaders();
    return this.http.get<UserDto>(`${this.urlAPI}/getByEmail/${email}`, {headers});
  }

  getAllUsers(): Observable<UserDto[]> {
    const headers = this.getHeaders();
    return this.http.get<UserDto[]>(`${this.urlAPI}/getAll}`, {headers});
  }

  updateUser(userId: string, userDTO: UserDto): Observable<any> {
    const headers = this.getHeaders();
    return this.http.put<any>(`${this.urlAPI}/update/${userId}`, userDTO, {headers})
      .pipe(
        map((response: any) => {
          if (response && response.body) {
            console.log('User updated successfully:', response.body);
          }
          return response;
        })
      );
  }

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({'Authorization': `Bearer ${token}`});
  }
}
