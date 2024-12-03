import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthGuard} from "./guards/AuthGuard.guard";
import {AuthComponent} from "./components/auth/Auth.component";
import {AdminGuard} from "./guards/AdminGuard.guard";
import {HomeComponent} from "./components/home-page/Home.component";
import {ProfileComponent} from "./components/profile-page/Profile.component";
import {MyDevicesComponent} from "./components/user-devices-page/MyDevices.component";
import {NotAuthComponent} from "./components/not-auth-page/NotAuth.component";
import {AdminUsersComponent} from "./components/admin-users-page/AdminUsers.component";
import {AdminDevicesComponent} from "./components/admin-devices-page/AdminDevices.component";

const routes: Routes = [
  {path: 'login', component: AuthComponent},
  {path: 'home', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'myProfile', component: ProfileComponent, canActivate: [AuthGuard]},
  {path: 'myDevices', component: MyDevicesComponent, canActivate: [AuthGuard]},
  {path: 'not-authorized', component: NotAuthComponent, canActivate: [AuthGuard]},
  {path: 'admin/all-users', component: AdminUsersComponent, canActivate: [AdminGuard, AuthGuard]},
  {path: 'admin/all-devices', component: AdminDevicesComponent, canActivate: [AdminGuard, AuthGuard]},
  {path: '**', redirectTo: 'home'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
