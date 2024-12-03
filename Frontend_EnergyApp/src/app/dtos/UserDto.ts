import {Role} from "./Role";

export class UserDto {
  id: string;
  firstName: string;
  secondName: string;
  email: string;
  role: Role;

  constructor(id: string, firstName: string, secondName: string, email: string, role: Role) {
    this.id = id;
    this.firstName = firstName;
    this.secondName = secondName;
    this.email = email;
    this.role = role;
  }
}
